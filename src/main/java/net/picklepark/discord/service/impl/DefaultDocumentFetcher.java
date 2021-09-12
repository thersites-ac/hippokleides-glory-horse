package net.picklepark.discord.service.impl;

import net.picklepark.discord.service.DocumentFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class DefaultDocumentFetcher implements DocumentFetcher {
    @Override
    public Document fetch(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
