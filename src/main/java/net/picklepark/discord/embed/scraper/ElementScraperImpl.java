package net.picklepark.discord.embed.scraper;

import net.picklepark.discord.embed.scraper.net.DocumentFetcher;
import net.picklepark.discord.embed.scraper.net.DocumentFetcherImpl;
import net.picklepark.discord.exception.NotFoundException;
import net.picklepark.discord.exception.NullDocumentException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElementScraperImpl implements ElementScraper {
    private static final String H2 = "h2";
    private static final String CORE_FEATS = "https://legacy.aonprd.com/coreRulebook/feats.html";
    private static final String ADVANCED_CLASS_FEATS = "https://legacy.aonprd.com/advancedClassGuide/feats.html";
    private static final String ADVANCED_PLAYER_FEATS = "https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html";

    private final DocumentFetcher fetcher;

    public ElementScraperImpl(DocumentFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public ElementScraperImpl() {
        this.fetcher = new DocumentFetcherImpl();
    }

    @Override
    public List<Element> scrapeCoreFeat(String id) throws IOException {
        Element element = getRootFeatElement(id);
        List<Element> elements = new ArrayList<>();
        while (null != element && !element.tagName().equals(H2)) {
            elements.add(element);
            element = element.nextElementSibling();
        }
        return elements;
    }

    private Element getRootFeatElement(String id) throws IOException {
        Document document = fetcher.fetch(CORE_FEATS);
        if (document == null)
            throw new NullDocumentException(CORE_FEATS);
        Element element = document.getElementById(id);
        if (element == null) {
            throw new NotFoundException(id, CORE_FEATS);
        }
        return element;
    }

}
