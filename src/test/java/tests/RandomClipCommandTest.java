package tests;

import net.picklepark.discord.command.audio.RandomClipCommand;
import tools.SpyDiscordActions;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Queue;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RandomClipCommandTest {

    private RandomClipCommand randomClipCommand;
    private SpyDiscordActions actions;
    private ClipManager clipManager;

    @Before
    public void setup() {
        clipManager = new ClipManagerImpl();
        randomClipCommand = new RandomClipCommand(clipManager);
        actions = new SpyDiscordActions();
    }

    @Test
    public void playsClip() throws DiscordCommandException {
        givenSomeClipExists();
        whenInvoke();
        thenPlaysSomeClip();
        thenSendsTitle();
    }

    @Test
    public void warnsOfNoClips() throws DiscordCommandException {
        givenNoClipsExist();
        whenInvoke();
        thenDoesNothingButComplain();
    }

    private void givenSomeClipExists() {
        clipManager.put(LocalClip.builder()
                .path("/example/foo")
                .title("/foo")
                .build());
        clipManager.put(LocalClip.builder()
                .path("/example/bar")
                .title("bar")
                .build());
    }

    private void givenNoClipsExist() {
        clipManager.clear();
    }

    private void whenInvoke() throws DiscordCommandException {
        actions.setUserInput("random");
        randomClipCommand.execute(actions);
    }

    private void thenPlaysSomeClip() {
        assertTrue(actions.isConnected());
        Queue<String> audioQueue = actions.getQueuedAudio();
        assertEquals(1, audioQueue.size());
        String uri = audioQueue.remove();
        assertTrue(clipManager.getAllCommandNames().stream()
                .anyMatch(name -> clipManager.lookup(name)
                        .getPath()
                        .equals(uri)));
    }

    private void thenSendsTitle() {
        assertEquals(1, actions.getSentMessage().size());
        assertTrue(clipManager.getAllCommandNames().stream()
                .anyMatch(name -> actions.getSentMessage()
                        .get(0)
                        .contains(name)));
    }

    private void thenDoesNothingButComplain() {
        assertFalse(actions.isConnected());
        assertEquals(0, actions.getQueuedAudio().size());
        assertEquals("I have never heard the sweet sound of your conversation.", actions.getSentMessage().get(0));
    }

}