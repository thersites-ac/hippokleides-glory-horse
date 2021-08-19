package net.picklepark.discord.embed.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public interface ElementScraper {
    public List<Element> scrapeFeatNodes(String id, String url) throws IOException;
    public List<Element> scrapeCoreSpell(String spellName) throws IOException;
}
