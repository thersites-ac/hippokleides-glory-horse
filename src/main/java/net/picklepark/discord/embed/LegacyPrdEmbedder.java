package net.picklepark.discord.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.ScrapeResult;
import net.picklepark.discord.embed.model.Spell;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.embed.transformer.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class LegacyPrdEmbedder implements PathfinderEmbedder {
    private static final String CORE_FEATS = "https://legacy.aonprd.com/coreRulebook/feats.html";
    private static final String ADVANCED_CLASS_FEATS = "https://legacy.aonprd.com/advancedClassGuide/feats.html";
    private static final String ADVANCED_PLAYER_FEATS = "https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html";

    private static final Logger logger = LoggerFactory.getLogger(LegacyPrdEmbedder.class);

    private final ElementScraper scraper;
    private final EmbedRenderer<Feat> featRenderer;
    private final Transformer<Feat> featTransformer;
    private final EmbedRenderer<Spell> spellRenderer;
    private final Transformer<Spell> spellTransformer;

    public LegacyPrdEmbedder(ElementScraper scraper,
                             EmbedRenderer<Feat> featRenderer,
                             Transformer<Feat> featTransformer,
                             EmbedRenderer<Spell> spellRenderer,
                             Transformer<Spell> spellTransformer) {
        this.scraper = scraper;
        this.featRenderer = featRenderer;
        this.featTransformer = featTransformer;
        this.spellRenderer = spellRenderer;
        this.spellTransformer = spellTransformer;
    }

    @Override
    public MessageEmbed embedCoreFeat(String id) throws IOException {
        return embedWithSource(id, CORE_FEATS, "Core Rulebook");
    }

    @Override
    public MessageEmbed embedAdvancedPlayerFeat(String id) throws IOException {
        return embedWithSource(id, ADVANCED_PLAYER_FEATS, "Advanced Player's Guide");
    }

    @Override
    public MessageEmbed embedAdvancedClassFeat(String id) throws IOException {
        return embedWithSource(id, ADVANCED_CLASS_FEATS, "Advanced Class Guide");
    }

    @Override
    public MessageEmbed embedSpell(String id) throws IOException {
        logger.info("Scraping {}", id);
        ScrapeResult result = scraper.scrapeCoreSpell(id);
        logger.info("Elements: {}", Arrays.toString(result.getElements().toArray()));
        Spell spell = spellTransformer.transform(result);
        logger.info("Spell: {}", spell.toString());
        return spellRenderer.render(spell);
    }

    private MessageEmbed embedWithSource(String id, String url, String source) throws IOException {
        logger.info("Scraping {}", id);
        ScrapeResult result = scraper.scrapeFeatNodes(id, url);
        logger.info("Elements: {}", Arrays.toString(result.getElements().toArray()));
        Feat feat = featTransformer.transform(result);
        logger.info("Feat: {}", feat.toString());
        return makeEmbed(feat, url, id, source);
    }

    private MessageEmbed makeEmbed(Feat feat, String baseUrl, String id, String author) {
        String url = baseUrl + "#" + id;
        return featRenderer.render(feat, url, author);
    }

}
