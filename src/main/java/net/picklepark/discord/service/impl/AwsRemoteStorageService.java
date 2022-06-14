package net.picklepark.discord.service.impl;

import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.RemoteStorageService;
import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.model.LocalClip;
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
    private final ClipManager clipManager;
    private final Map<String, Map<String, String>> remoteKeys;
    private final Duration timeToLive;
    private final String clipsDirectory;

    @Inject
    public AwsRemoteStorageService(@Named("s3.client.download") S3Client downloadClient,
                                   @Named("s3.client.upload") S3Client storageClient,
                                   @Named("s3.bucket.uploads") String uploadsBucket,
                                   @Named("s3.bucket.trimmed") String clipsBucket,
                                   @Named("s3.uploads.ttl") Duration timeToLive,
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
    public LocalClip download(String canonicalKey) throws ResourceNotFoundException, IOException {
        Optional<String> maybeTitle = readTitle(canonicalKey);
        if (maybeTitle.isPresent())
            return downloadAs(canonicalKey, maybeTitle.get());
        else
            throw new ResourceNotFoundException(canonicalKey, "s3://" + clipsBucket + "/" + canonicalKey);
    }

    private LocalClip downloadAs(String canonicalKey, String title) throws IOException {
        // fixme: vomit
        String guild = canonicalKey.split("/")[0];

        putRemoteKey(canonicalKey, title);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(clipsBucket)
                .key(canonicalKey)
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

    private void putRemoteKey(String canonicalKey, String title) {
        // fixme: gross
        String guild = canonicalKey.split("/")[0];
        remoteKeys.computeIfAbsent(guild, g -> new ConcurrentHashMap<>());
        remoteKeys.get(guild).put(title, canonicalKey);
    }

    private Optional<String> readTitle(String canonicalKey) {
        logger.info("Checking to download {}/{}", clipsBucket, canonicalKey);
        GetObjectTaggingRequest taggingRequest = GetObjectTaggingRequest.builder()
                .bucket(clipsBucket)
                .key(canonicalKey)
                .build();
        GetObjectTaggingResponse taggingResponse = trimmedClipsClient.getObjectTagging(taggingRequest);

        return taggingResponse.tagSet().stream()
                .filter(t -> t.key().equals("title"))
                .findFirst()
                .map(Tag::value);
    }

    private String toCanonicalKey(String guild, String objectKey) {
        return guild + "/" + objectKey;
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
    public void delete(String guild, String title) {
        Map<String, String> guildTitles = remoteKeys.get(guild);
        if (guild != null) {
            String canonicalKey = guildTitles.get(title);
            logger.info("Deleting {} with AWS key {}", title, canonicalKey);
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(clipsBucket)
                    .key(canonicalKey)
                    .build();
            trimmedClipsClient.deleteObject(request);
            deleteLocal(canonicalKey);
        }
    }

    private void deleteLocal(String filename) {
        File localCopy = new File(localPathOf(filename));
        if (localCopy.exists())
            localCopy.delete();
    }

    private void downloadAll(List<S3Object> contents) {
        for (S3Object object: contents) {
            try {
                LocalClip clip = syncOneFile(object.key());
                clipManager.put(clip);
            } catch (ResourceNotFoundException | IOException e) {
                logger.error("While attempting to download " + object.key(), e);
            }
        }
    }

    private LocalClip syncOneFile(String canonicalKey) throws ResourceNotFoundException, IOException {
        logger.info("Syncing " + canonicalKey);
        File file = new File(localPathOf(canonicalKey));
        if (!file.exists())
            return download(canonicalKey);
        else
            return nameLocalFile(canonicalKey);
    }

    private LocalClip nameLocalFile(String key) throws ResourceNotFoundException {
        // fixme: yuuuuck
        String guild = key.split("/")[0];
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
        throw new ResourceNotFoundException(key, "s3://" + clipsBucket + "/" + key);

    }

    private String upload(String guild, File file) {
        String canonicalKey = toCanonicalKey(guild, file.getName());
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

    private String localPathOf(String key) {
        return clipsDirectory + "/" + key;
    }
}