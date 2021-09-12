package net.picklepark.discord.service;

import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.model.ScrapeResult;

import java.io.IOException;

public interface ElementScraper {
    public ScrapeResult scrapeFeatNodes(String id, String url) throws IOException, ResourceNotFoundException;
    public ScrapeResult scrapeCoreSpell(String spellName) throws IOException, ResourceNotFoundException;
}
