package net.picklepark.discord.scraper;

import net.dv8tion.jda.api.entities.MessageEmbed;
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
public class PathfinderScraperTests {

    private Embedder embedder;
    private List<Element> elements;

    @Before
    public void setup() {
        embedder = new Embedder(new MockElementScraper());
        elements = new ArrayList<>();
    }

    @Test
    public void createsEmbed() throws IOException {
        MessageEmbed embed = embedder.embedFeat("foo");
    }

    @Test
    public void usesIdForAuthor() throws IOException {
        MessageEmbed embed = embedder.embedFeat("foo");
        Assert.assertEquals("foo", embed.getAuthor());
    }


    private class MockElementScraper implements ElementScraper {

        @Override
        public List<Element> scrapeCoreFeat(String id) throws IOException {
            return elements;
        }
    }
}
