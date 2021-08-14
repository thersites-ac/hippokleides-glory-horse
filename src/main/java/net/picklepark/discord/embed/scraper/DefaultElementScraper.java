package net.picklepark.discord.embed.scraper;

import net.picklepark.discord.embed.scraper.net.DocumentFetcher;
import net.picklepark.discord.embed.scraper.net.DefaultDocumentFetcher;
import net.picklepark.discord.exception.NotFoundException;
import net.picklepark.discord.exception.NullDocumentException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultElementScraper implements ElementScraper {
    private static final String H2 = "h2";

    private final DocumentFetcher fetcher;

    public DefaultElementScraper(DocumentFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public DefaultElementScraper() {
        this.fetcher = new DefaultDocumentFetcher();
    }

    @Override
    public List<Element> scrapeFeatNodes(String id, String url) throws IOException {
        Element element = getRootFeatElement(id, url);
        List<Element> elements = new ArrayList<>();
        do {
            elements.add(element);
            element = element.nextElementSibling();
        } while (null != element && !element.tagName().equals(H2));
        return elements;
    }

    private Element getRootFeatElement(String id, String url) throws IOException {
        Document document = fetcher.fetch(url);
        if (document == null)
            throw new NullDocumentException(url);
        Element element = document.getElementById(id);
        if (element == null) {
            throw new NotFoundException(id, url);
        }
        return element;
    }

}
