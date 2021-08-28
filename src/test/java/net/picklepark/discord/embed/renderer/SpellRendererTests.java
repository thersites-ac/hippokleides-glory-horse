package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Spell;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class SpellRendererTests {

    private EmbedRenderer<Spell> renderer;
    private Spell spell;
    private MessageEmbed embed;

    @Before
    public void canCreate() {
        renderer = new SpellRenderer();
    }

    @Test
    public void spellNameBecomesTitle() {
        givenScrapedSpell();
        whenRender();
        thenEmbedTitleIsSpellName();
    }

    @Test
    public void spellDescriptionBecomesFinalField() {
        givenScrapedSpell();
        whenRender();
        thenLastFieldIsDescription();
    }

    @Test
    public void spellQualifiersBecomeNonfinalFields() {
        givenScrapedSpell();
        whenRender();
        thenInitialFieldsAreQualifiers();
    }

    private void thenInitialFieldsAreQualifiers() {
        Map<String, String> qualifiers = spell.getQualifiers();
        List<MessageEmbed.Field> fields = embed.getFields();
        Assert.assertEquals(qualifiers.keySet().size(), fields.size() - 1);
        for (int i = 0; i < fields.size() - 1; i++) {
            MessageEmbed.Field field = fields.get(i);
            Assert.assertEquals(field.getValue(), qualifiers.get(field.getName()));
            Assert.assertTrue(field.isInline());
        }
    }

    private void thenLastFieldIsDescription() {
        MessageEmbed.Field lastField = embed.getFields().get(embed.getFields().size() - 1);
        Assert.assertEquals("Spell Description", lastField.getName());
        Assert.assertEquals(spell.getDescription(), lastField.getValue());
        Assert.assertFalse(lastField.isInline());
    }

    private void givenScrapedSpell() {
        Map<String, String> qualifiers = new HashMap<>();
        qualifiers.put("Level", "sorcerer/wizard 1");
        spell = Spell.builder()
                .name("Magic Missile")
                .qualifiers(qualifiers)
                .description("It's pretty good")
                .build();
    }

    private void whenRender() {
        embed = renderer.render(spell, "http://www.somewhere.com", "Me");
    }

    private void thenEmbedTitleIsSpellName() {
        Assert.assertEquals(spell.getName(), embed.getTitle());
    }

}
