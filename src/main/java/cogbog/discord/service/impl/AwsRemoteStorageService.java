package cogbog.discord.service.impl;

import cogbog.discord.exception.MalformedKeyException;
import cogbog.discord.exception.ResourceNotFoundException;
import cogbog.discord.model.CanonicalKey;
import cogbog.discord.model.LocalClip;
import cogbog.discord.service.ClipManager;
import cogbog.discord.exception.NoSuchClipException;
import cogbog.discord.service.RemoteStorageService;
import cogbog.discord.model.Coordinates;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AwsRemoteStorageService implements RemoteStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsRemoteStorageService.class);

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

    @Inject
    public AwsRemoteStorageService(@Named("s3.download.client") S3Client downloadClient,
                                   @Named("s3.upload.client") S3Client storageClient,
                                   @Named("s3.upload.bucket") String uploadsBucket,
                                   @Named("s3.upload.ttl") Duration timeToLive,
                                   @Named("s3.trimmed.bucket") String clipsBucket,
                                   @Named("clips.directory") String clipsDirectory,
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
        remoteKeys = new ConcurrentHashMap<>();
    }

    @Override
    public Coordinates store(String guild, File file) {
        String canonicalKey = upload(guild, file);
        URL url = presignedUrlFor(canonicalKey);
        return Coordinates.builder()
                .key(file.getName())
                .prefix(guild)
                .url(url)
                .build();
    }

    @Override
    public LocalClip download(CanonicalKey key) throws ResourceNotFoundException, IOException {
        Optional<String> maybeTitle = readTitle(key);
        if (maybeTitle.isPresent())
            return downloadAs(key, maybeTitle.get());
        else
            throw new ResourceNotFoundException(key.toString(), "s3://" + clipsBucket + "/" + key.toString());
    }

    private LocalClip downloadAs(CanonicalKey canonicalKey, String title) throws IOException {
        String guild = canonicalKey.getGuild();

        putRemoteKey(canonicalKey, title);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(clipsBucket)
                .key(canonicalKey.toString())
                .build();

        logger.info("Downloading {}", title);
        String path = localPathOf(canonicalKey);
        new File(path).getParentFile().mkdirs();
        logger.info("Made dirs: " + path);
        InputStream inputStream = trimmedClipsClient.getObject(request);
        Files.copy(inputStream, Path.of(path));
        inputStream.close();
        return LocalClip.builder()
                .guild(guild)
                .path(path)
                .title(title)
                .build();
    }

    private void putRemoteKey(CanonicalKey canonicalKey, String title) {
        String guild = canonicalKey.getGuild();
        remoteKeys.computeIfAbsent(guild, g -> new ConcurrentHashMap<>());
        remoteKeys.get(guild).put(title, canonicalKey);
    }

    private Optional<String> readTitle(CanonicalKey canonicalKey) {
        logger.info("Checking to download {}/{}", clipsBucket, canonicalKey);
        GetObjectTaggingRequest taggingRequest = GetObjectTaggingRequest.builder()
                .bucket(clipsBucket)
                .key(canonicalKey.toString())
                .build();
        GetObjectTaggingResponse taggingResponse = trimmedClipsClient.getObjectTagging(taggingRequest);

        return taggingResponse.tagSet().stream()
                .filter(t -> t.key().equals("title"))
                .findFirst()
                .map(Tag::value);
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
        Optional<String> maybeTitle = readTitle(key);
        if (maybeTitle.isPresent()) {
            String title = maybeTitle.get();
            putRemoteKey(key, title);
            return LocalClip.builder()
                    .path(path)
                    .guild(guild)
                    .title(title)
                    .build();
        }
        throw new ResourceNotFoundException(key.toString(), "s3://" + clipsBucket + "/" + key);

    }

    private String upload(String guild, File file) {
        String canonicalKey = CanonicalKey.builder()
                .guild(guild)
                .key(file.getName())
                .build().toString();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(uploadsBucket)
                .key(canonicalKey)
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