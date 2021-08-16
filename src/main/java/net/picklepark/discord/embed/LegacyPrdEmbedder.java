package net.picklepark.discord.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.embed.transformer.FeatTransformer;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LegacyPrdEmbedder implements PathfinderEmbedder {
    private static final String CORE_FEATS = "https://legacy.aonprd.com/coreRulebook/feats.html";
    private static final String ADVANCED_CLASS_FEATS = "https://legacy.aonprd.com/advancedClassGuide/feats.html";
    private static final String ADVANCED_PLAYER_FEATS = "https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html";

    private static final Logger logger = LoggerFactory.getLogger(LegacyPrdEmbedder.class);

    private ElementScraper scraper;
    private EmbedRenderer renderer;
    private FeatTransformer transformer;

    public LegacyPrdEmbedder(ElementScraper scraper, EmbedRenderer renderer, FeatTransformer transformer) {
        this.scraper = scraper;
        this.renderer = renderer;
        this.transformer = transformer;
    }

    @Override
    public MessageEmbed embedCoreFeat(String id) throws IOException {
        logger.info("Scraping {}", id);
        List<Element> elements = scraper.scrapeFeatNodes(id, CORE_FEATS);
        logger.info("Elements: {}", Arrays.toString(elements.toArray()));
        Feat feat = transformer.transformFeat(elements);
        logger.info("Feat: {}", feat.toString());
        return makeEmbed(feat, CORE_FEATS, id, "Core Rulebook");
    }

    private MessageEmbed makeEmbed(Feat feat, String baseUrl, String id, String author) {
        String url = baseUrl + "#" + id;
        return renderer.renderFeat(feat, url, author);
    }

    @Override
    public MessageEmbed embedAdvancedPlayerFeat(String id) throws IOException {
        logger.info("Scraping {}", id);
        List<Element> elements = scraper.scrapeFeatNodes(id, ADVANCED_PLAYER_FEATS);
        logger.info("Elements: {}", Arrays.toString(elements.toArray()));
        Feat feat = transformer.transformFeat(elements);
        logger.info("Feat: {}", feat.toString());
        return makeEmbed(feat, ADVANCED_PLAYER_FEATS, id, "Advanced Player's Guide");
    }

    @Override
    public MessageEmbed embedAdvancedClassFeat(String id) throws IOException {
        logger.info("Scraping {}", id);
        List<Element> elements = scraper.scrapeFeatNodes(id, ADVANCED_CLASS_FEATS);
        logger.info("Elements: {}", Arrays.toString(elements.toArray()));
        Feat feat = transformer.transformFeat(elements);
        logger.info("Feat: {}", feat.toString());
        return makeEmbed(feat, ADVANCED_CLASS_FEATS, id, "Advanced Class Guide");
    }

}
