package net.picklepark.discord.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.scraper.ElementScraper;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public class Embedder {

    private ElementScraper scraper;

    public Embedder(ElementScraper scraper) {
        this.scraper = scraper;
    }

    public MessageEmbed embedFeat(String id) throws IOException {
        List<Element> elements = scraper.scrapeCoreFeat(id);
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(id)
                .build();
        return embed;
    }
}
