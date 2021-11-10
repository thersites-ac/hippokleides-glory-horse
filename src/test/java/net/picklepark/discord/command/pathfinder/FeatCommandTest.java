package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.service.impl.TestEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    private void givenEmbedderReturnsDifferentResults() {
        testEmbedder.addEmbed(new EmbedBuilder()
                .setTitle("First")
                .addField("Order", "First", false)
        .build());
        testEmbedder.addEmbed(new EmbedBuilder()
                .setTitle("Second")
                .addField("Order", "Second", false)
        .build());
    }

    private void whenRunTwice() {
        featCommand.execute(actions);
        featCommand.execute(actions);
    }

    private void thenSentBothResults() {
        List<MessageEmbed> embeds = actions.getSentEmbeds();
        assertEquals(2, embeds.size());
        assertEquals("First", embeds.get(0).getTitle());
        assertEquals("Second", embeds.get(1).getTitle());
    }

}