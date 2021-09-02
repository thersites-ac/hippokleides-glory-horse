package net.picklepark.discord.service;

import java.io.File;
import java.net.URI;

public interface StorageService {
    public URI store(File file);
}
