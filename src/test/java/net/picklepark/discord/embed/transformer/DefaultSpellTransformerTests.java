package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Spell;
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
    private List<Element> input;
    private Spell spell;

    @Before
    public void canCreate() {
        transformer = new DefaultSpellTransformer();
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

    private void thenOutputHasCorrectQualifiers() {
        Map<String, String> qualifiers = spell.getQualifiers();
        Assert.assertEquals("sorcerer/wizard 1", qualifiers.get("Level"));
        Assert.assertEquals("evocation", qualifiers.get("School"));
    }

    private void givenSloppyElements() {
        givenValidElements();
        input.get(1)
                .appendChild(new Element("b").text("Level"))
                .appendChild(new TextNode("sorcerer/wizard 1"));
    }

    private void thenNoFieldIsNull() {
        Assert.assertNotNull(spell.getName());
        Assert.assertNotNull(spell.getDescription());
        Assert.assertNotNull(spell.getQualifiers());
    }

    private void givenValidElements() {
        input = new ArrayList<>();
        input.add(new Element("p")
                .appendChild(new Element("b").text("Magic Missile")));
        input.add(new Element("p").addClass("stat-block-1")
                .appendChild(new Element("b").text("School"))
                .appendChild(new TextNode("evocation")));
        input.add(new Element("p").text("Description: a great spell"));
    }

    private void whenParseSpell() {
        spell = transformer.transform(input);
    }

}
