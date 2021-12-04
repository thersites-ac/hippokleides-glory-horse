package net.picklepark.discord.tests;

import net.picklepark.discord.model.ScrapeResult;
import net.picklepark.discord.model.Spell;
import net.picklepark.discord.service.Transformer;
import net.picklepark.discord.service.impl.DefaultSpellTransformer;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class DefaultSpellTransformerTests {

    private Transformer<Spell> transformer;
    private List<Element> elements;
    private ScrapeResult input;
    private Spell spell;

    @Before
    public void setup() {
        transformer = new DefaultSpellTransformer();
        elements = new ArrayList<>();
        input = ScrapeResult.builder()
                .elements(elements)
                .build();
    }

    @Test
    public void producesNonNullFields() {
        givenValidElements();
        whenParseSpell();
        thenNoFieldIsNull();
    }

    @Test
    public void canParseMultipleQualifiersInOneParagraph() {
        givenSloppyElements();
        whenParseSpell();
        thenOutputHasCorrectQualifiers();
    }

    @Test
    public void recordsSourceData() {
        givenScaperReportedSourceData();
        whenParseSpell();
        thenOutputHasSourceData();
    }

    private void thenOutputHasSourceData() {
        Assert.assertEquals(input.getSource(), spell.getSource());
        Assert.assertEquals(input.getUrl(), spell.getUrl());
    }

    private void givenScaperReportedSourceData() {
        givenValidElements();
        input.setSource("foo");
        input.setUrl("bar");
    }

    private void thenOutputHasCorrectQualifiers() {
        Map<String, String> qualifiers = spell.getQualifiers();
        Assert.assertEquals("sorcerer/wizard 1", qualifiers.get("Level"));
        Assert.assertEquals("evocation", qualifiers.get("School"));
    }

    private void givenSloppyElements() {
        givenValidElements();
        elements.get(1)
                .appendChild(new Element("b").text("Level"))
                .appendChild(new TextNode("sorcerer/wizard 1"));
    }

    private void thenNoFieldIsNull() {
        Assert.assertNotNull(spell.getName());
        Assert.assertNotNull(spell.getDescription());
        Assert.assertNotNull(spell.getQualifiers());
    }

    private void givenValidElements() {
        elements.add(new Element("p")
                .appendChild(new Element("b").text("Magic Missile")));
        elements.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("School"))
                .appendChild(new TextNode("evocation")));
        elements.add(new Element("p").text("Description: a great spell"));
    }

    private void whenParseSpell() {
        spell = transformer.transform(input);
    }

}
