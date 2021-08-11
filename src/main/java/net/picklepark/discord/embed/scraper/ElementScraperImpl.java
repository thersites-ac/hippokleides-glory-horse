package net.picklepark.discord.embed.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElementScraperImpl implements ElementScraper {
    private static final String H2 = "h2";
    private static final String CORE_FEATS = "https://legacy.aonprd.com/coreRulebook/feats.html";
    private static final String ADVANCED_CLASS_FEATS = "https://legacy.aonprd.com/advancedClassGuide/feats.html";
    private static final String ADVANCED_PLAYER_FEATS = "https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html";

    @Override
    public List<Element> scrapeCoreFeat(String id) throws IOException {
        Element first = Jsoup.connect(CORE_FEATS)
                .get()
                .getElementById(id);
        List<Element> elements = new ArrayList<>();
        elements.add(first);
        Element sibling = first.nextElementSibling();
        while (null != sibling && !sibling.tagName().equals(H2)) {
            elements.add(sibling);
            sibling = sibling.nextElementSibling();
        }
        return elements;
    }

}
