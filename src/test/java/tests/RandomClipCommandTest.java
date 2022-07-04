package tests;

import cogbog.discord.command.audio.RandomClipCommand;
import tools.SpyMessageReceivedActions;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.LocalClip;
import cogbog.discord.service.ClipManager;
import cogbog.discord.service.impl.ClipManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Queue;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RandomClipCommandTest {

    private static final String GUILD = "guild";

    private RandomClipCommand randomClipCommand;
    private SpyMessageReceivedActions actions;
    private ClipManager clipManager;

    @Before
    public void setup() {
        clipManager = new ClipManagerImpl(null);
        randomClipCommand = new RandomClipCommand(clipManager, null);
        actions = new SpyMessageReceivedActions();
        actions.setGuildName(GUILD);
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
                .guild(GUILD)
                .build());
        clipManager.put(LocalClip.builder()
                .path("/example/bar")
                .title("bar")
                .guild(GUILD)
                .build());
    }

    private void givenNoClipsExist() {
        clipManager.clear(GUILD);
    }

    private void whenInvoke() throws DiscordCommandException {
        actions.setUserInput("random");
        actions.connect();
        randomClipCommand.execute(actions);
    }

    private void thenPlaysSomeClip() {
        assertTrue(actions.isConnected());
        Queue<String> audioQueue = actions.getQueuedAudio();
        assertEquals(1, audioQueue.size());
        String uri = audioQueue.remove();
        assertTrue(clipManager.getAllCommandNames(GUILD).stream()
                .anyMatch(name -> clipManager.lookup(GUILD, name)
                        .getPath()
                        .equals(uri)));
    }

    private void thenSendsTitle() {
        assertEquals(1, actions.getSentMessage().size());
        assertTrue(clipManager.getAllCommandNames(GUILD).stream()
                .anyMatch(name -> actions.getSentMessage()
                        .get(0)
                        .contains(name)));
    }

    private void thenDoesNothingButComplain() {
        assertEquals(0, actions.getQueuedAudio().size());
        assertEquals("I have never heard the sweet sound of your conversation.", actions.getSentMessage().get(0));
    }

}