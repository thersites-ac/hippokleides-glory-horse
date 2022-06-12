package tests;

import net.picklepark.discord.command.audio.NukeQueueCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.SpyDiscordActions;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class NukeQueueCommandTest {

    @Test
    public void emptiesQueue() throws DiscordCommandException, NotEnoughQueueCapacityException {
        SpyDiscordActions actions = new SpyDiscordActions();
        actions.queue("foo");
        new NukeQueueCommand().execute(actions);
        assertEquals(0, actions.getQueuedAudio().size());
        assertEquals(1, actions.getSentMessage().size());
        assertEquals(NukeQueueCommand.CONFIRMATION_MESSAGE, actions.getSentMessage().get(0));
    }

}