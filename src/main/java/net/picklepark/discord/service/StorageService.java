package net.picklepark.discord.service;

import net.picklepark.discord.service.model.Coordinates;

import java.io.File;
import java.net.MalformedURLException;

public interface StorageService {
    public Coordinates store(File file) throws MalformedURLException;
}
