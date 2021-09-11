package net.picklepark.discord.service.impl;

import net.picklepark.discord.model.ScrapeResult;
import net.picklepark.discord.service.DocumentFetcher;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.exception.NullDocumentException;
import net.picklepark.discord.service.ElementScraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultElementScraper implements ElementScraper {
    private static final String H2 = "h2";
    private static final String CORE_SPELL_LIST = "https://legacy.aonprd.com/coreRulebook/spellLists.html";
    private static final String coreRulebookUrl = "https://legacy.aonprd.com/coreRulebook/";

    private final DocumentFetcher fetcher;

    public DefaultElementScraper(DocumentFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public DefaultElementScraper() {
        this.fetcher = new DefaultDocumentFetcher();
    }

    @Override
    public ScrapeResult scrapeFeatNodes(String name, String url) throws IOException {
        String id = convertToId(name);
        Element element = getRootFeatElement(id, url);
        List<Element> elements = new ArrayList<>();
        do {
            elements.add(element);
            element = element.nextElementSibling();
        } while (null != element && !element.tagName().equals(H2));
        return ScrapeResult.builder()
                .elements(elements)
                .url(url + "#" + id)
                .build();
    }

    @Override
    public ScrapeResult scrapeCoreSpell(String name) throws IOException {
       Element link = findIndexTagFor(name);
       String suffix = extractSuffix(link);
       String spellUrl = coreRulebookUrl + suffix;
       Document page = fetcher.fetch(spellUrl);
       List<Element> elements = extractSpellElements(name, page);
       return ScrapeResult.builder()
               .elements(elements)
               .source("Core Rulebook")
               .url(spellUrl)
               .build();
    }

    private List<Element> extractSpellElements(String name, Document page) {
        List<Element> elements = new ArrayList<>();
        String id = convertToId(name);
        Element element = page.getElementById(id);
        if (element == null)
            throw new ResourceNotFoundException(name, page.baseUri());
        elements.add(element);
        element = element.nextElementSibling();
        while (element != null && ! element.hasAttr("id") && ! element.hasClass("footer")) {
            elements.add(element);
            element = element.nextElementSibling();
        }
        return elements;
    }

    private String convertToId(String name) {
        return name.replace(' ', '-').toLowerCase();
    }

    private String extractSuffix(Element link) {
        return link.attributes().get("href");
    }

    private Element findIndexTagFor(String spellName) throws IOException {
        Document index = fetcher.fetch(CORE_SPELL_LIST);
        return index.getElementsByTag("a").stream()
                .filter(e -> e.text().equalsIgnoreCase(spellName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(spellName, CORE_SPELL_LIST));
    }

    private Element getRootFeatElement(String id, String url) throws IOException {
        Document document = fetcher.fetch(url);
        if (document == null)
            throw new NullDocumentException(url);
        Element element = document.getElementById(id);
        if (element == null) {
            throw new ResourceNotFoundException(id, url);
        }
        return element;
    }

}
