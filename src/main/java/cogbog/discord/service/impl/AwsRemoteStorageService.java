package cogbog.discord.service.impl;

import cogbog.discord.adaptor.DataPersistenceAdaptor;
import cogbog.discord.exception.MalformedKeyException;
import cogbog.discord.exception.ResourceNotFoundException;
import cogbog.discord.model.CanonicalKey;
import cogbog.discord.model.ClipMetadata;
import cogbog.discord.model.LocalClip;
import cogbog.discord.service.ClipManager;
import cogbog.discord.exception.NoSuchClipException;
import cogbog.discord.service.RemoteStorageService;
import cogbog.discord.model.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Singleton
public class AwsRemoteStorageService implements RemoteStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsRemoteStorageService.class);

    private static final String TITLE = "title";

    private final S3Client trimmedClipsClient;
    private final S3Presigner presigner;
    private final S3Client untrimmedClipsClient;
    private final String uploadsBucket;
    private final String clipsBucket;
    // fixme: both classes depend on the other
    private final ClipManager clipManager;
    private final Map<String, Map<String, CanonicalKey>> remoteKeys;
    private final Duration timeToLive;
    private final String clipsDirectory;
    private final ExecutorService executorService;
    private final DataPersistenceAdaptor<Recording> recordingDataPersistenceAdaptor;

    @Inject
    public AwsRemoteStorageService(@Named("s3.client.download") S3Client downloadClient,
                                   @Named("s3.client.upload") S3Client storageClient,
                                   @Named("s3.bucket.name.upload") String uploadsBucket,
                                   @Named("s3.bucket.name.trimmed") String clipsBucket,
                                   @Named("s3.signature.ttl") Duration timeToLive,
                                   @Named("clips.directory") String clipsDirectory,
                                   DataPersistenceAdaptor<Recording> recordingDataPersistenceAdaptor,
                                   ExecutorService executorService,
                                   S3Presigner presigner,
                                   ClipManager clipManager) {
        this.uploadsBucket = uploadsBucket;
        this.clipsBucket = clipsBucket;
        this.trimmedClipsClient = downloadClient;
        this.untrimmedClipsClient = storageClient;
        this.presigner = presigner;
        this.clipManager = clipManager;
        this.timeToLive = timeToLive;
        this.clipsDirectory = clipsDirectory;
        this.executorService = executorService;
        this.recordingDataPersistenceAdaptor = recordingDataPersistenceAdaptor;
        remoteKeys = new ConcurrentHashMap<>();
    }

    @Override
    public Recording store(String guild, File file, ClipMetadata metadata) {
        String canonicalKey = upload(guild, file, metadata);
        URL url = presignedUrlFor(canonicalKey);
        var recording = Recording.builder()
                .key(file.getName())
                .prefix(guild)
                .recordingUri(url)
                .recordingId(metadata.getRecordingId())
                .build();
        recordingDataPersistenceAdaptor.write(recording);
        return recording;
    }

    @Override
    public LocalClip download(CanonicalKey key) throws ResourceNotFoundException {
        var futureTags = executorService.submit(() -> collectTags(key));
        var successfulDownload = executorService.submit(() -> downloadClip(key));
        try {
            var tags = futureTags.get();
            String title = tags.get(TITLE);
            // fixme: temporary solution; probably should add some content here
            if (title == null)
                title = key.getKey().split(".")[0];
            putRemoteKey(key, title);
            if (successfulDownload.get()) {
                return LocalClip.builder()
                        .guild(key.getGuild())
                        .path(localPathOf(key))
                        .title(title)
                        .metadata(ClipMetadata.fromMap(tags))
                        .build();
            } else {
                throw new ResourceNotFoundException(key.toString(), clipsBucket);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ResourceNotFoundException(key.toString(), e);
        }
    }

    private boolean downloadClip(CanonicalKey canonicalKey) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(clipsBucket)
                .key(canonicalKey.toString())
                .build();
        logger.info("Downloading {}", canonicalKey.toString());
        String path = localPathOf(canonicalKey);
        if (new File(path).getParentFile().mkdirs())
            logger.info("Made dirs for " + path);
        try (InputStream inputStream = trimmedClipsClient.getObject(request)) {
            Files.copy(inputStream, Path.of(path));
            return true;
        } catch (IOException e) {
            logger.error("While downloading " + canonicalKey.toString(), e);
            return false;
        }
    }

    private void putRemoteKey(CanonicalKey canonicalKey, String title) {
        String guild = canonicalKey.getGuild();
        remoteKeys.computeIfAbsent(guild, g -> new ConcurrentHashMap<>());
        remoteKeys.get(guild).put(title, canonicalKey);
    }

    private Map<String, String> collectTags(CanonicalKey canonicalKey) {
        logger.info("Getting tags for {}/{}", clipsBucket, canonicalKey);
        GetObjectTaggingRequest taggingRequest = GetObjectTaggingRequest.builder()
                .bucket(clipsBucket)
                .key(canonicalKey.toString())
                .build();
        GetObjectTaggingResponse taggingResponse = trimmedClipsClient.getObjectTagging(taggingRequest);

        Map<String, String> result = new HashMap<>();
        taggingResponse.tagSet().forEach(tag -> result.put(tag.key(), tag.value()));
        return result;
    }

    @Override
    public void sync(String guild) {
        clipManager.clear(guild);
        ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(clipsBucket)
                .prefix(guild + "/")
                .build();
        ListObjectsResponse response = trimmedClipsClient.listObjects(request);
        downloadAll(response.contents());
    }

    @Override
    public void delete(String guild, String title) throws NoSuchClipException {
        Map<String, CanonicalKey> guildTitles = remoteKeys.get(guild);
        if (guild != null && guildTitles.get(title) != null) {
            CanonicalKey canonicalKey = guildTitles.get(title);
            logger.info("Deleting {} with AWS key {}", title, canonicalKey);
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(clipsBucket)
                    .key(canonicalKey.toString())
                    .build();
            trimmedClipsClient.deleteObject(request);
            deleteLocal(canonicalKey);
        } else {
            throw new NoSuchClipException(guild, title);
        }
    }

    private void deleteLocal(CanonicalKey key) {
        File localCopy = new File(localPathOf(key));
        if (localCopy.exists())
            localCopy.delete();
    }

    private void downloadAll(List<S3Object> contents) {
        for (S3Object object: contents) {
            try {
                var key = CanonicalKey.fromString(object.key());
                LocalClip clip = syncOneFile(key);
                clipManager.put(clip);
            } catch (ResourceNotFoundException | IOException | MalformedKeyException e) {
                logger.error("While attempting to download " + object.key(), e);
            }
        }
    }

    private LocalClip syncOneFile(CanonicalKey canonicalKey) throws ResourceNotFoundException, IOException {
        logger.info("Syncing " + canonicalKey);
        File file = new File(localPathOf(canonicalKey));
        if (!file.exists())
            return download(canonicalKey);
        else
            return nameLocalFile(canonicalKey);
    }

    private LocalClip nameLocalFile(CanonicalKey key) throws ResourceNotFoundException {
        String guild = key.getGuild();
        logger.info("{} already exists; looking up tagged title", key);
        String path = localPathOf(key);
        var tags = collectTags(key);
        String title = tags.get(TITLE);
        if (title != null) {
            putRemoteKey(key, title);
            return LocalClip.builder()
                    .path(path)
                    .guild(guild)
                    .title(title)
                    .metadata(ClipMetadata.fromMap(tags))
                    .build();
        }
        throw new ResourceNotFoundException(key.toString(), "s3://" + clipsBucket + "/" + key);

    }

    private String upload(String guild, File file, ClipMetadata metadata) {
        String canonicalKey = CanonicalKey.builder()
                .guild(guild)
                .key(file.getName())
                .build().toString();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(uploadsBucket)
                .key(canonicalKey)
                .tagging(metadata.toTagging())
                .storageClass(StorageClass.STANDARD)
                .build();
        untrimmedClipsClient.putObject(request, Path.of(file.toURI()));
        return canonicalKey;
    }

    private URL presignedUrlFor(String canonicalKey) {
        GetObjectRequest basicRequest = GetObjectRequest.builder()
                .bucket(uploadsBucket)
                .key(canonicalKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(timeToLive)
                .getObjectRequest(basicRequest)
                .build();

        PresignedGetObjectRequest output = presigner.presignGetObject(presignRequest);

        return output.url();
    }

    private String localPathOf(CanonicalKey key) {
        return clipsDirectory + "/" + key.toString();
    }
}