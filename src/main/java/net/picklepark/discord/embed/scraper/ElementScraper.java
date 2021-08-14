package net.picklepark.discord.embed.scraper;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public interface ElementScraper {
    List<Element> scrapeFeatNodes(String id, String url) throws IOException;
}
