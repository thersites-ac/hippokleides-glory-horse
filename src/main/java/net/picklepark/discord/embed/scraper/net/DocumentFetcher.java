package net.picklepark.discord.embed.scraper.net;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface DocumentFetcher {
    public Document fetch(String url) throws IOException;
}
