package net.picklepark.discord.service;

import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.model.LocalClip;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public interface StorageService {
    public Coordinates store(File file) throws MalformedURLException;
    LocalClip download(String bucketName, String objectKey) throws URISyntaxException;
}
