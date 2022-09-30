package tools;

import cogbog.discord.exception.NoSuchClipException;
import cogbog.discord.exception.ResourceNotFoundException;
import cogbog.discord.model.CanonicalKey;
import cogbog.discord.model.ClipMetadata;
import cogbog.discord.model.Recording;
import cogbog.discord.model.LocalClip;
import cogbog.discord.service.RemoteStorageService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpyStorageService implements RemoteStorageService {

    private final Map<CanonicalKey, Recording> clips = new HashMap<>();

    private final String id;

    public SpyStorageService(String id) {
        this.id = id;
    }

    public SpyStorageService() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public Recording store(String guild, File file, ClipMetadata metadata) throws MalformedURLException {
        var key = CanonicalKey.builder()
                .guild(guild)
                .key(file.getName())
                .build();
        var url = new URL("http://cogbog.com/" + key.getGuild() + "/" + key.getKey());
        var value = Recording.builder()
                .key(key.getKey())
                .prefix(key.getGuild())
                .recordingUri(url)
                .recordingId(id)
                .build();
        clips.put(key, value);
        return value;
    }

    @Override
    public LocalClip download(CanonicalKey key) throws URISyntaxException, ResourceNotFoundException, IOException {
        var matched = clips.get(key);
        if (matched == null)
            throw new ResourceNotFoundException();
        else
            return LocalClip.builder()
                    .metadata(null)
                    .guild(key.getGuild())
                    .path(matched.getPrefix())
                    .title(matched.getKey())
                    .build();
    }

    @Override
    public void sync(String guild) {}

    @Override
    public void delete(String guild, String key) throws NoSuchClipException {
    }
}
