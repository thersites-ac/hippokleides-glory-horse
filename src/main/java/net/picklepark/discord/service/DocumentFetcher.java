package net.picklepark.discord.service;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface DocumentFetcher {
    public Document fetch(String url) throws IOException;
}
