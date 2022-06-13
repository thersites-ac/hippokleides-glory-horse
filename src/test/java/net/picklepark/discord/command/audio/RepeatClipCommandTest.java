package net.picklepark.discord.command.audio;

import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.SpyMessageReceivedActions;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RepeatClipCommandTest {

    private static final String CLIP_TITLE = "wow";
    private static final String CLIP_URI = "/clips/wow.wav";
    private static final LocalClip CLIP = LocalClip.builder()
            .title(CLIP_TITLE)
            .path(CLIP_URI)
            .build();

    private final ClipManager clipManager = new ClipManagerImpl();
    private final RepeatClipCommand command = new RepeatClipCommand(clipManager);
    private SpyMessageReceivedActions actions;

    @Before
    public void setup() {
        actions = new SpyMessageReceivedActions();
        actions.setArg(RepeatClipCommand.ARGUMENT_NUMBER, "42");
        actions.setArg(RepeatClipCommand.ARGUMENT_TITLE, CLIP_TITLE);
        clipManager.put(CLIP);
    }

    @Test
    public void testHappyPath() throws DiscordCommandException {
        command.execute(actions);
        assertEquals(42, actions.getQueuedAudio().size());
        actions.getQueuedAudio().forEach(item -> assertEquals(CLIP_URI, item));
        assertEquals(1, actions.getSentMessage().size());
        assertEquals(RepeatClipCommand.CONFIRMATION_MESSAGE, actions.getSentMessage().get(0));
    }

    @Test
    public void explainsBadClipInputs() throws DiscordCommandException {
        String unmappedTitle = "something bad";
        actions.setArg(RepeatClipCommand.ARGUMENT_TITLE, unmappedTitle);
        command.execute(actions);
        assertEquals(0, actions.getQueuedAudio().size());
        assertEquals(1, actions.getSentMessage().size());
        assertEquals(String.format(RepeatClipCommand.BAD_CLIP_INPUT_MESSAGE, unmappedTitle), actions.getSentMessage().get(0));
    }

    @Test
    public void explainsBadNumberInputs() throws DiscordCommandException {
        explainsBadNumberInput("-2");
        explainsBadNumberInput("2.4");
        explainsBadNumberInput("0");
        explainsBadNumberInput("two");
    }

    @Test
    public void respectsMaximumQueueSize() throws DiscordCommandException {
        actions.setArg(RepeatClipCommand.ARGUMENT_NUMBER, Integer.MAX_VALUE + "");
        command.execute(actions);
        assertEquals(0, actions.getQueuedAudio().size());
        assertEquals(1, actions.getSentMessage().size());
        assertEquals(RepeatClipCommand.INSUFFICIENT_QUEUE_SPACE, actions.getSentMessage().get(0));
    }

    private void explainsBadNumberInput(String input) throws DiscordCommandException {
        setup();
        actions.setArg(RepeatClipCommand.ARGUMENT_NUMBER, input);
        command.execute(actions);

        assertEquals(0, actions.getQueuedAudio().size());
        actions.getQueuedAudio().forEach(item -> assertEquals(CLIP_URI, item));
        assertEquals(1, actions.getSentMessage().size());
        assertEquals(String.format(RepeatClipCommand.BAD_NUMBER_INPUT_MESSAGE, input), actions.getSentMessage().get(0));
    }

}