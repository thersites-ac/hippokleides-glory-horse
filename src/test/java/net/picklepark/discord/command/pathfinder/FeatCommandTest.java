package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.service.impl.TestEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class FeatCommandTest {

    private SpyDiscordActions actions;
    private FeatCommand featCommand;
    private TestEmbedder testEmbedder;

    @Before
    public void setup() {
        testEmbedder = new TestEmbedder();
        featCommand = new FeatCommand(testEmbedder);
        actions = new SpyDiscordActions();
        actions.setArg("feat", "foo");
    }

    @Test
    public void recyclable() {
        givenEmbedderReturnsDifferentResults();
        whenRunTwice();
        thenSentBothResults();
    }

    @Test
    public void triesBasicRulebook() {
        givenFeatFromBasicRulebook();
        whenLookupFeat();
        thenSendsResultFor("Core");
    }

    @Test
    public void triesAdvancedPlayerRulebook() {
        givenFeatFromAdvancedPlayerRulebook();
        whenLookupFeat();
        thenSendsResultFor("Advanced Player");
    }

    @Test
    public void triesAdvancedClassRulebook() {
        givenFeatFromAdvancedClassRulebook();
        whenLookupFeat();
        thenSendsResultFor("Advanced Class");
    }

    private void givenFeatFromAdvancedClassRulebook() {
        testEmbedder.addAdvancedClass(new EmbedBuilder()
                .setTitle("Advanced Class")
                .build());
    }

    private void givenFeatFromAdvancedPlayerRulebook() {
        testEmbedder.addAdvancedPlayer(new EmbedBuilder()
                .setTitle("Advanced Player")
                .build());
    }

    private void givenFeatFromBasicRulebook() {
        testEmbedder.addCore(new EmbedBuilder()
                .setTitle("Core")
                .build());
    }

    private void givenEmbedderReturnsDifferentResults() {
        testEmbedder.addEmbed(new EmbedBuilder()
                .setTitle("First")
                .build());
        testEmbedder.addEmbed(new EmbedBuilder()
                .setTitle("Second")
                .build());
    }

    private void whenLookupFeat() {
        featCommand.execute(actions);
    }

    private void whenRunTwice() {
        whenLookupFeat();
        whenLookupFeat();
    }

    private void thenSendsResultFor(String title) {
        MessageEmbed embed = actions.getSentEmbeds().remove(0);
        assertEquals(title, embed.getTitle());
    }

    private void thenSentBothResults() {
        thenSendsResultFor("First");
        thenSendsResultFor("Second");
    }

}