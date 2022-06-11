package tests;

import net.picklepark.discord.command.audio.QueueAudioCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.SpyDiscordActions;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class QueueAudioCommandTest {

    private static final String CLIP_URI = "./some/clip.wav";

    private SpyDiscordActions actions;
    private QueueAudioCommand command;

    @Before
    public void setup() {
        command = new QueueAudioCommand();
        actions = new SpyDiscordActions();
        actions.setArg(QueueAudioCommand.ARGUMENT, CLIP_URI);
    }

    @Test
    public void expectedPreAndPostStates() {
        assertFalse(actions.isConnected());
        command.execute(actions);
        assertEquals(1, actions.getQueuedAudio().size());
        assertEquals(CLIP_URI, actions.getQueuedAudio().remove());
        assertTrue(actions.isConnected());
        assertEquals(1, actions.getSentMessage().size());
        assertEquals(QueueAudioCommand.CONFIRMATION_MESSAGE, actions.getSentMessage().get(0));
    }

}