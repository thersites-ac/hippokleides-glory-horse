package cogbog.discord.service.impl;

import cogbog.discord.service.DocumentFetcher;
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
