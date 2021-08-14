package net.picklepark.discord.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.renderer.DefaultRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.embed.transformer.DefaultFeatTransformer;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class LegacyPrdEmbedderTests {

    private List<Element> elements;
    private LegacyPrdEmbedder legacyPrdEmbedder;

    @Before
    public void setup() {
        makeValidElements();
        legacyPrdEmbedder = new LegacyPrdEmbedder(new MockElementScraper(), new DefaultRenderer(), new DefaultFeatTransformer());
    }

    @Test
    public void canCreateAndRun() throws IOException {
        new LegacyPrdEmbedder(new MockElementScraper(), new MockRenderer(), new DefaultFeatTransformer()).embedCoreFeat("foo");
    }

    @Test
    public void createsValidEmbedWithRealRenderer() throws IOException {
        MessageEmbed embed = legacyPrdEmbedder.embedCoreFeat("foo");
        Assert.assertEquals("foo", embed.getTitle());
        Assert.assertEquals("some text", embed.getDescription());
        Assert.assertEquals("bar", embed.getFields().get(0).getName());
        Assert.assertEquals("baz", embed.getFields().get(0).getValue());
        Assert.assertEquals("quux", embed.getFooter().getText());
    }

    @Test
    public void ifNoDetailsThenNoFields() throws IOException {
        elements = elements.stream()
                .filter(e -> !e.hasClass("stat-block-1"))
                .collect(Collectors.toList());
        MessageEmbed embed = legacyPrdEmbedder.embedCoreFeat("foo");
        Assert.assertTrue(embed.getFields().isEmpty());
    }

    @Test
    public void footerNotRequired() throws IOException {
        elements = elements.stream()
                .filter(e -> !e.hasClass("stat-block-2"))
                .collect(Collectors.toList());
        MessageEmbed embed = legacyPrdEmbedder.embedCoreFeat("foo");
        Assert.assertEquals("Scraped with love by Hippokleides, Glory Horse", embed.getFooter().getText());
    }

    @Test
    public void unwrapsExtraNodesInDetail() throws IOException {
        extendDetailElement();
        MessageEmbed embed = legacyPrdEmbedder.embedCoreFeat("foo");
        Assert.assertEquals("bazextra", embed.getFields().get(0).getValue());
    }

    private void makeValidElements() {
        elements = new ArrayList<>();
        elements.add(new Element("h2").text("foo"));
        elements.add(new Element("p").text("some text"));
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("bar"))
                .appendChild(new TextNode("baz")));
        elements.add(new Element("p").addClass("stat-block-2").text("quux"));
    }

    private void extendDetailElement() {
        elements.get(2).appendChild(new Element("a").text("extra"));
    }

    private class MockElementScraper implements ElementScraper {
        @Override
        public List<Element> scrapeFeatNodes(String id, String url) {
            return elements;
        }
    }

    private class MockRenderer implements EmbedRenderer {
        @Override
        public MessageEmbed renderFeat(Feat feat) {
            return null;
        }
    }

}
