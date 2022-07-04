package cogbog.discord.service;

import cogbog.discord.exception.ResourceNotFoundException;
import cogbog.discord.model.ScrapeResult;

import java.io.IOException;

public interface ElementScraper {
    public ScrapeResult scrapeFeatNodes(String id, String url) throws IOException, ResourceNotFoundException;
    public ScrapeResult scrapeCoreSpell(String spellName) throws IOException, ResourceNotFoundException;
}
