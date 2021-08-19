package net.picklepark.discord.embed.scraper;

import net.picklepark.discord.embed.scraper.net.DocumentFetcher;
import net.picklepark.discord.embed.scraper.net.DefaultDocumentFetcher;
import net.picklepark.discord.exception.NotFoundException;
import net.picklepark.discord.exception.NullDocumentException;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DefaultElementScraper implements ElementScraper {
    private static final String H2 = "h2";
    private static final String CORE_SPELL_LIST = "https://legacy.aonprd.com/coreRulebook/spellLists.html";
    private static final String urlPrefix = "https://legacy.aonprd.com/coreRulebook/";

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

    @Override
    public List<Element> scrapeCoreSpell(String name) throws IOException {
       Element link = findIndexTagFor(name);
       String suffix = extractSuffix(link);
       Document page = fetcher.fetch(urlPrefix + suffix);
       return extractSpellElements(name, page);
    }

    private List<Element> extractSpellElements(String name, Document page) {
        throw new RuntimeException("IMPLEMENT ME!!!!");
    }

    private String extractSuffix(Element link) {
        return link.attributes().get("href");
    }

    private Element findIndexTagFor(String spellName) throws IOException {
        Document index = fetcher.fetch(CORE_SPELL_LIST);
        return index.getElementsByTag("a").stream()
                .filter(e -> e.text().equalsIgnoreCase(spellName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(spellName, CORE_SPELL_LIST));
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
