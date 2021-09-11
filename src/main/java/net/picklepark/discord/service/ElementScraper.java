package net.picklepark.discord.service;

import net.picklepark.discord.model.ScrapeResult;

import java.io.IOException;

public interface ElementScraper {
    public ScrapeResult scrapeFeatNodes(String id, String url) throws IOException;
    public ScrapeResult scrapeCoreSpell(String spellName) throws IOException;
}
