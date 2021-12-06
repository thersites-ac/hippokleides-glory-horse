package net.picklepark.discord.service;

import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.model.LocalClip;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

// FIXME: separate the logic of how to store and download from filenames and folders
public interface RemoteStorageService {
    Coordinates store(File file) throws MalformedURLException;
    LocalClip download(String objectKey) throws URISyntaxException, ResourceNotFoundException, IOException;
    void sync();
    void delete(String key);
}
