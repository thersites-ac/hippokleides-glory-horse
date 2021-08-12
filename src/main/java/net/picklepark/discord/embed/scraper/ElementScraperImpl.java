package net.picklepark.discord.embed.scraper;

import net.picklepark.discord.embed.scraper.net.DocumentFetcher;
import net.picklepark.discord.embed.scraper.net.DocumentFetcherImpl;
import net.picklepark.discord.exception.NotFoundException;
import org.jsoup.nodes.Element;

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
        Element element = fetcher.fetch(CORE_FEATS)
                .getElementById(id);
        List<Element> elements = new ArrayList<>();
        if (element == null) {
            throw new NotFoundException(id, CORE_FEATS);
        }
        while (null != element && !element.tagName().equals(H2)) {
            elements.add(element);
            element = element.nextElementSibling();
        }
        return elements;
    }

}
