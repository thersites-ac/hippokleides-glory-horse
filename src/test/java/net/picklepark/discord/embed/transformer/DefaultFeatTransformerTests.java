package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.ScrapeResult;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class DefaultFeatTransformerTests {

    private List<Element> elements;
    private ScrapeResult input;
    private Transformer<Feat> transformer;
    private Feat result;

    @Before
    public void setup() {
        elements = new ArrayList<>();
        input = ScrapeResult.builder()
                .elements(elements)
                .build();
        transformer = new DefaultFeatTransformer();
    }

    @Test(expected = ScrapedElementValidationException.class)
    public void summaryBlockRequired() {
        givenScrapedFeat();
        givenFeatHasNoSummary();
        whenTransformToFeat();
    }

    @Test(expected = ScrapedElementValidationException.class)
    public void limitedNameOptionsForDetailBlocks() {
        givenScrapedFeat();
        givenScrapedElementHasSpuriousDetail();
        whenTransformToFeat();
    }

    @Test
    public void ignoresCssClassOfDetailBlocks() {
        ignoresClass("stat-block-1");
        ignoresClass("stat-block-2");
    }

    private void ignoresClass(String cssClass) {
        givenScrapedFeat();
        givenAllElementsHaveClass(cssClass);
        whenTransformToFeat();
        thenAllFieldsAreCorrect();
    }

    private void givenAllElementsHaveClass(String cssClass) {
        input.getElements().forEach(e -> e.addClass(cssClass));
    }

    @Test
    public void handlesEmbeddedBenefits() {
        givenScrapedFeat();
        givenScrapedElementHasBenefits();
        whenTransformToFeat();
        thenBenefitsAppearInFeatDetails();
    }

    private void givenScrapedFeat() {
        elements.clear();
        elements.add(new Element("h2").text("Whirlwind attack"));
        elements.add(new Element("p").text("Description of the feat"));
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("Prerequisites:"))
                .appendChild(new TextNode("Dex 69")));
    }

    private void givenScrapedElementHasBenefits() {
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("Benefits:"))
                .appendChild(new TextNode("Pick one of the below options.")));
        elements.add(new Element("p").addClass("stat-block-2")
                .appendChild(new Element("i").text("Skilled bridge player: "))
                .appendChild(new TextNode("You play bridge really well.")));
    }

    private void givenScrapedElementHasSpuriousDetail() {
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("Failure:"))
                .appendChild(new TextNode("This should cause an exception.")));
    }

    private void givenFeatHasNoSummary() {
        elements.remove(1);
    }

    private void whenTransformToFeat() {
        result = transformer.transform(input);
    }

    private void thenAllFieldsAreCorrect() {
        Assert.assertEquals("Whirlwind attack", result.getName());
        Assert.assertEquals("Description of the feat", result.getDescription());
        Assert.assertEquals("Prerequisites:", result.getFeatDetails().get(0).getName());
        Assert.assertEquals("Dex 69", result.getFeatDetails().get(0).getText());
    }

    private void thenBenefitsAppearInFeatDetails() {
        Assert.assertTrue(result.getFeatDetails().get(1).getText().contains("Skilled bridge player: You play bridge really well."));
    }

}
