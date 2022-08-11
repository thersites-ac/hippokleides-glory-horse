package cogbog.discord.service;

import cogbog.discord.exception.ResourceNotFoundException;
import cogbog.discord.model.CanonicalKey;
import cogbog.discord.model.ClipMetadata;
import cogbog.discord.model.LocalClip;
import cogbog.discord.exception.NoSuchClipException;
import cogbog.discord.model.Recording;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

// todo: separate the logic of how to store and download from filenames and folders
public interface RemoteStorageService {
    Recording store(String guild, File file, ClipMetadata metadata) throws MalformedURLException;
    LocalClip download(CanonicalKey key) throws URISyntaxException, ResourceNotFoundException, IOException;
    void sync(String guild);
    void delete(String guild, String key) throws NoSuchClipException;
}
