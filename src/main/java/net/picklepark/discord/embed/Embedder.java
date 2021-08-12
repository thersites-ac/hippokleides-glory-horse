package net.picklepark.discord.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class Embedder {

    private ElementScraper scraper;
    private EmbedRenderer renderer;

    public Embedder(ElementScraper scraper,
                    EmbedRenderer renderer) {
        this.scraper = scraper;
        this.renderer = renderer;
    }

    public MessageEmbed embedFeat(String id) throws IOException {
        List<Element> elements = scraper.scrapeCoreFeat(id);
        return renderer.renderCoreFeat(elements);
    }
}
