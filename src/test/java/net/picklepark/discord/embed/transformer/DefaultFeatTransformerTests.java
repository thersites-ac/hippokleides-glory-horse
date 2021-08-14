package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Feat;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class DefaultFeatTransformerTests {

    List<Element> elements;
    FeatTransformer transformer;
    Feat result;

    @Before
    public void setup() {
        elements = new ArrayList<>();
        transformer = new DefaultFeatTransformer();
    }

    @Test
    public void detailBlocksOptionalForCoreFeat() {
        givenScrapedElementsHaveNoDetailBlocks();
        whenTransformToFeat();
        thenCoreFeatHasNoDetails();
    }

    @Test
    public void footerBlockOptionalForCoreFeat() {
        givenScrapedElementsHaveNoFooter();
        whenTransformToFeat();
        thenFeatHasDefaultFooter();
    }

    private void thenFeatHasDefaultFooter() {
        Assert.assertEquals("Scraped with love by Hippokleides, Glory Horse", result.getFooter());
    }

    private void givenScrapedElementsHaveNoFooter() {
        givenScrapedCoreFeat();
        filterOut("stat-block-2");
    }

    private void filterOut(String htmlClass) {
        elements = elements.stream()
                .filter(e -> !e.hasClass(htmlClass))
                .collect(Collectors.toList());
    }

    private void givenScrapedCoreFeat() {
        elements.add(new Element("h2").text("foo"));
        elements.add(new Element("p").text("some text"));
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("bar"))
                .appendChild(new TextNode("baz")));
        elements.add(new Element("p").addClass("stat-block-2").text("quux"));
    }
    private void givenScrapedElementsHaveNoDetailBlocks() {
        givenScrapedCoreFeat();
        filterOut("stat-block-1");
    }

    private void whenTransformToFeat() {
        result = transformer.transformCoreFeat(elements);
    }

    private void thenCoreFeatHasNoDetails() {
        Assert.assertTrue(result.getFeatDetails().isEmpty());
    }

}
