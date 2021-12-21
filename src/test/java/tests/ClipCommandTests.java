package tests;

import net.picklepark.discord.command.audio.ClipCommand;
import org.junit.Before;
import org.junit.Test;
import tools.SpyDiscordActions;

import static org.junit.Assert.assertEquals;

public class ClipCommandTests {

    private SpyDiscordActions actions;
    private ClipCommand command;

    @Before
    public void setup() {
        actions = new SpyDiscordActions();
        command = new ClipCommand("some clip");
    }

    @Test
    public void queuesToChannelTwo() {
        command.execute(actions);

        assertEquals(1, actions.getChannelTwo().size());
        assertEquals("some clip", actions.getChannelTwo().remove());
    }
}
