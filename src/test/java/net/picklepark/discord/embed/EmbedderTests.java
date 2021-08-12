package net.picklepark.discord.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.embed.Embedder;
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
public class EmbedderTests {

    private Embedder embedder = new Embedder(new MockElementScraper(), new MockRenderer());
    private List<Element> elements;

    @Before
    public void setup() {
        elements = new ArrayList<>();
    }

    // this is really a renderer test
    @Test
    public void createsEmbed() throws IOException {
        embedder.embedFeat("foo");
    }

    // this is really a renderer test
    @Test
    public void usesIdForAuthor() throws IOException {
        MessageEmbed embed = embedder.embedFeat("foo");
        Assert.assertEquals("foo", embed.getAuthor());
    }


    private class MockElementScraper implements ElementScraper {
        @Override
        public List<Element> scrapeCoreFeat(String id) {
            return elements;
        }
    }

    private class MockRenderer implements EmbedRenderer {
        @Override
        public MessageEmbed renderCoreFeat(List<Element> elements) {
            return null;
        }
    }
}
