package net.picklepark.discord.embed.scraper.net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DefaultDocumentFetcher implements DocumentFetcher {
    @Override
    public Document fetch(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
