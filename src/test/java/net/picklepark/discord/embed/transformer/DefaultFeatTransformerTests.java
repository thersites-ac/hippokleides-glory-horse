package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.embed.model.Subrule;
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

    private List<Element> elements;
    private FeatTransformer transformer;
    private Feat result;

    @Before
    public void setup() {
        elements = new ArrayList<>();
        transformer = new DefaultFeatTransformer();
    }

    @Test
    public void detailBlocksOptionalForCoreFeat() {
        givenScrapedElementsHaveNoDetailBlocks();
        whenTransformToCoreFeat();
        thenResultHasNoDetails();
    }

    @Test
    public void footerBlockOptionalForCoreFeat() {
        givenScrapedElementsHaveNoFooter();
        whenTransformToCoreFeat();
        thenFeatHasDefaultFooter();
    }

    @Test
    public void detailBlocksOptionalForAdvancedPlayerFeat() {
        givenScrapedElementsHaveNoDetailBlocks();
        whenTransformToAdvancedPlayerFeat();
        thenResultHasNoDetails();
    }

    @Test
    public void footerBlockOptionalForAdvancedPlayerFeat() {
        givenScrapedElementsHaveNoFooter();
        whenTransformToAdvancedPlayerFeat();
        thenFeatHasDefaultFooter();
    }

    @Test
    public void expectsStrongTagInAdvancedClassFeat() {
        givenScrapedAdvancedClassFeat();
        whenTransformToAdvancedClassFeat();
        thenFeatHasCorrectDetails();
    }

    private void thenFeatHasCorrectDetails() {
        Assert.assertEquals(1, result.getFeatDetails().size());
        Assert.assertEquals("detail name", result.getFeatDetails().get(0).getName());
        Assert.assertEquals("detail body", result.getFeatDetails().get(0).getText());
    }

    private void whenTransformToAdvancedClassFeat() {
        result = transformer.transformAdvancedClassFeat(elements);
    }

    @Test
    public void handlesEmbeddedRulesInAdvancedPlayerFeat() {
        givenScrapedElementHasEmbeddedRules();
        whenTransformToAdvancedPlayerFeat();
        thenRulesAppearInFeatDetails();
        thenFeatHasCorrectFooter();
    }

    private void thenFeatHasCorrectFooter() {
        Assert.assertEquals("footer", result.getFooter());
    }

    private void thenRulesAppearInFeatDetails() {
        Assert.assertEquals(1, result.getFeatDetails().size());
        List<Subrule> subrules = result.getFeatDetails().get(0).getSubrules();
        Assert.assertEquals(2, subrules.size());
        Assert.assertEquals("rule name", subrules.get(0).getName());
        Assert.assertEquals("rule body", subrules.get(0).getText());
        Assert.assertEquals("another rule name", subrules.get(1).getName());
        Assert.assertEquals("another rule body", subrules.get(1).getText());
    }

    private void givenScrapedElementHasEmbeddedRules() {
        elements.add(new Element("h2").text("title"));
        elements.add(new Element("p").text("some text"));
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("detail name"))
                .appendChild(new TextNode("detail body")));
        elements.add(new Element("p").addClass("stat-block-2")
                .appendChild(new Element("i").text("rule name"))
                .appendChild(new TextNode("rule body")));
        elements.add(new Element("p").addClass("stat-block-2")
                .appendChild(new Element("i").text("another rule name"))
                .appendChild(new TextNode("another rule body")));
        elements.add(new Element("p").addClass("stat-block-2").text("footer"));
        insertInBody();
    }

    @Test
    public void handlesParagraphsWithNoTitleInAdvancedPlayerFeat() {
        Assert.fail();
    }

    @Test
    public void handlesParagraphsWithNoTitleInAdvancedClassFeat() {
        Assert.fail();
    }

    @Test
    public void handlesStatBlock2forAdvancedPlayerFeats() {
        Assert.fail();
    }

    private void givenScrapedAdvancedClassFeat() {
        elements.add(new Element("h2").text("title"));
        elements.add(new Element("p").text("some text"));
        elements.add(new Element("p")
                .appendChild(new Element("strong").text("detail name"))
                .appendChild(new TextNode("detail body")));
        insertInBody();
    }

    private void insertInBody() {
        Element parent = new Element("body");
        elements.forEach(parent::appendChild);
    }

    private void whenTransformToAdvancedPlayerFeat() {
        result = transformer.transformAdvancedPlayerFeat(elements);
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
        elements.add(new Element("h2").text("title"));
        elements.add(new Element("p").text("some text"));
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("detail name"))
                .appendChild(new TextNode("detail body")));
        elements.add(new Element("p").addClass("stat-block-2").text("footer"));
        insertInBody();
    }

    private void givenScrapedElementsHaveNoDetailBlocks() {
        givenScrapedCoreFeat();
        filterOut("stat-block-1");
    }

    private void whenTransformToCoreFeat() {
        result = transformer.transformCoreFeat(elements);
    }

    private void thenResultHasNoDetails() {
        Assert.assertTrue(result.getFeatDetails().isEmpty());
    }

}
