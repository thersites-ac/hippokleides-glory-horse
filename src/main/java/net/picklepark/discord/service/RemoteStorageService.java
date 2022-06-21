package net.picklepark.discord.service;

import net.picklepark.discord.exception.NoSuchClipException;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.model.CanonicalKey;
import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.model.LocalClip;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

// todo: separate the logic of how to store and download from filenames and folders
public interface RemoteStorageService {
    Coordinates store(String guild, File file) throws MalformedURLException;
    LocalClip download(CanonicalKey key) throws URISyntaxException, ResourceNotFoundException, IOException;
    void sync(String guild);
    void delete(String guild, String key) throws NoSuchClipException;
}
