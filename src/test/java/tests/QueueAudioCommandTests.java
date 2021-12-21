package tests;

import net.picklepark.discord.command.audio.QueueAudioCommand;
import org.junit.Before;
import org.junit.Test;
import tools.SpyDiscordActions;

import static org.junit.Assert.assertEquals;

public class QueueAudioCommandTests {

    private QueueAudioCommand command;
    private SpyDiscordActions actions;

    @Before
    public void setup() {
        command = new QueueAudioCommand();
        actions = new SpyDiscordActions();
    }

    @Test
    public void queuesToChannelOne() {
        actions.setArg("uri", "some song");

        command.execute(actions);

        assertEquals(1, actions.getChannelOne().size());
        assertEquals("some song", actions.getChannelOne().remove());
    }
}
