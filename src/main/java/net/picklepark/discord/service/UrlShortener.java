package net.picklepark.discord.service;

import java.io.IOException;

public interface UrlShortener {
    String shorten(String url) throws IOException;
}
