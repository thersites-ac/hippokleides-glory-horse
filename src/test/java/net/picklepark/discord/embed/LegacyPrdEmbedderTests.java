package net.picklepark.discord.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.renderer.FeatRenderer;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.embed.transformer.Transformer;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class LegacyPrdEmbedderTests {

    private Feat transformerReturns;
    private MessageEmbed rendererReturns;
    private List<Element> scraperReturns;
    private MessageEmbed result;
    private LegacyPrdEmbedder legacyPrdEmbedder;

    @Before
    public void setup() {
        legacyPrdEmbedder = new LegacyPrdEmbedder(new MockElementScraper(), new FeatRenderer(), new MockFeatTransformer());
        rendererReturns = new EmbedBuilder()
                .setDescription("foo")
                .build();
        scraperReturns = new ArrayList<>();
    }

    @Test
    public void addsSourceToCoreFeat() throws IOException {
        givenTransformerReturns("whirlwind-attack");
        whenEmbedCoreFeat("whirlwind-attack");
        thenResultHasSource("Core Rulebook");
    }

    @Test
    public void addsSourceToAdvancedPlayerFeat() throws IOException {
        givenTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedPlayerFeat("whirlwind-attack");
        thenResultHasSource("Advanced Player's Guide");
    }

    @Test
    public void addsSourceToAdvancedClassFeat() throws IOException {
        givenTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedClassFeat("whirlwind-attack");
        thenResultHasSource("Advanced Class Guide");
    }

    @Test
    public void addsAuthorLinkToCoreFeat() throws IOException {
        givenTransformerReturns("whirlwind-attack");
        whenEmbedCoreFeat("whirlwind-attack");
        thenResultHasAuthorLink("https://legacy.aonprd.com/coreRulebook/feats.html#whirlwind-attack");
    }

    @Test
    public void addsAuthorLinkToAdvancedClassFeat() throws IOException {
        givenTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedClassFeat("whirlwind-attack");
        thenResultHasAuthorLink("https://legacy.aonprd.com/advancedClassGuide/feats.html#whirlwind-attack");
    }

    @Test
    public void addsAuthorLinkToAdvancedPlayerFeat() throws IOException {
        givenTransformerReturns("whirlwind-attack");
        whenEmbedAdvancedPlayerFeat("whirlwind-attack");
        thenResultHasAuthorLink("https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html#whirlwind-attack");
    }

    private void givenTransformerReturns(String input) {
       transformerReturns = Feat.builder()
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
        Assert.assertEquals(url,
                result.getAuthor().getUrl());
    }

    private class MockElementScraper implements ElementScraper {
        @Override
        public List<Element> scrapeFeatNodes(String id, String url) {
            return scraperReturns;
        }

        @Override
        public List<Element> scrapeCoreSpell(String spellName) {
            return null;
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
        public Feat transform(List<Element> elements) {
            return transformerReturns;
        }
    }

}
