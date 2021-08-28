package net.picklepark.discord.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.ScrapeResult;
import net.picklepark.discord.embed.model.Spell;
import net.picklepark.discord.embed.renderer.FeatRenderer;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.renderer.SpellRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.embed.transformer.Transformer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(JUnit4.class)
public class LegacyPrdEmbedderTests {

    private Feat feat;
    private Spell spell;
    private MessageEmbed rendererReturns;
    private ScrapeResult scraperReturns;
    private MessageEmbed result;
    private LegacyPrdEmbedder legacyPrdEmbedder;

    @Before
    public void setup() {
        legacyPrdEmbedder = new LegacyPrdEmbedder(new MockElementScraper(), new FeatRenderer(), new MockFeatTransformer(), new SpellRenderer(), new MockSpellTransformer());
        rendererReturns = new EmbedBuilder()
                .setDescription("foo")
                .build();
        scraperReturns = ScrapeResult.builder()
                .elements(new ArrayList<>())
                .build();
    }

    @Test
    public void addsSourceToCoreFeat() throws IOException {
        givenFeatTransformerReturns("whirlwind-attack");
        whenEmbedCoreFeat("whirlwind-attack");
        thenResultHasSource("Core Rulebook");
    }

    @Test
    public void addsSourceToAdvancedPlayerFeat() throws IOException {
        givenFeatTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedPlayerFeat("whirlwind-attack");
        thenResultHasSource("Advanced Player's Guide");
    }

    @Test
    public void addsSourceToAdvancedClassFeat() throws IOException {
        givenFeatTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedClassFeat("whirlwind-attack");
        thenResultHasSource("Advanced Class Guide");
    }

    @Test
    public void addsAuthorLinkToCoreFeat() throws IOException {
        givenFeatTransformerReturns("whirlwind-attack");
        whenEmbedCoreFeat("whirlwind-attack");
        thenResultHasAuthorLink("https://legacy.aonprd.com/coreRulebook/feats.html#whirlwind-attack");
    }

    @Test
    public void addsAuthorLinkToAdvancedClassFeat() throws IOException {
        givenFeatTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedClassFeat("whirlwind-attack");
        thenResultHasAuthorLink("https://legacy.aonprd.com/advancedClassGuide/feats.html#whirlwind-attack");
    }

    @Test
    public void addsAuthorLinkToAdvancedPlayerFeat() throws IOException {
        givenFeatTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedPlayerFeat("whirlwind-attack");
        thenResultHasAuthorLink("https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html#whirlwind-attack");
    }

    private void givenFeatTransformerReturns(String input) {
       feat = Feat.builder()
               .name(input)
               .description(input)
               .featDetails(new ArrayList<>())
               .build();
    }

    private void whenEmbedCoreFeat(String feat) throws IOException {
        result = legacyPrdEmbedder.embedCoreFeat(feat);
    }

    private void whenEmbedAdvancedPlayerFeat(String feat) throws IOException {
        result = legacyPrdEmbedder.embedAdvancedPlayerFeat(feat);
    }

    private void whenEmbedAdvancedClassFeat(String feat) throws IOException {
        result = legacyPrdEmbedder.embedAdvancedClassFeat(feat);
    }

    private void thenResultHasSource(String source) {
        Assert.assertEquals(source, result.getAuthor().getName());
    }

    private void thenResultHasAuthorLink(String url) {
        Assert.assertEquals(url, result.getAuthor().getUrl());
    }

    private class MockElementScraper implements ElementScraper {
        @Override
        public ScrapeResult scrapeFeatNodes(String id, String url) {
            return scraperReturns;
        }

        @Override
        public ScrapeResult scrapeCoreSpell(String spellName) {
            return scraperReturns;
        }
    }

    private class MockRenderer implements EmbedRenderer<Feat> {
        @Override
        public MessageEmbed render(Feat feat, String url, String author) {
            return rendererReturns;
        }
    }

    private class MockFeatTransformer implements Transformer<Feat> {
        @Override
        public Feat transform(ScrapeResult elements) {
            return feat;
        }
    }

    private class MockSpellTransformer implements Transformer<Spell> {
        @Override
        public Spell transform(ScrapeResult elements) {
            return spell;
        }
    }
}
