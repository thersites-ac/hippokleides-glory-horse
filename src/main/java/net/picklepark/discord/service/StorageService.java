package net.picklepark.discord.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public interface StorageService {
    public URL store(File file) throws MalformedURLException;
}
