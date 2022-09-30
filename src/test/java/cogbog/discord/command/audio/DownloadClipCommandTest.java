package cogbog.discord.command.audio;

import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.LocalClip;
import cogbog.discord.service.ClipManager;
import cogbog.discord.service.impl.ClipManagerImpl;
import org.junit.Test;
import tools.SpyStorageService;

import static org.junit.Assert.*;

public class DownloadClipCommandTest extends CommandTest {

    private static final String TITLE = "title";
    private static final String ANOTHER_TITLE = "another title";
    private static final String INVALID_TITLE = "invalid title";
    private static final String GUILD = "guild";

    private static final LocalClip TEST_CLIP = LocalClip.builder()
            .path("foo")
            .title(TITLE)
            .guild(GUILD)
            .build();

    private static final LocalClip ANOTHER_TEST_CLIP = LocalClip.builder()
            .path("bar")
            .title(ANOTHER_TITLE)
            .guild(GUILD)
            .build();

    DownloadClipCommand command;

    @Test
    public void sendsFile() throws DiscordCommandException {
        command.execute(actions);
        fileWasSent(TEST_CLIP);
    }

    // we'd like to confirm it was a response to the input message, but too much refactoring the test tools

    @Test
    public void sendsAnotherFile() throws DiscordCommandException {
        setInput(ANOTHER_TITLE);
        command.execute(actions);
        fileWasSent(ANOTHER_TEST_CLIP);
    }

    @Test
    public void confusedWhenClipDoesNotExist() throws DiscordCommandException {
        setInput(INVALID_TITLE);
        command.execute(actions);
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("I don't know any clip by the name of " + INVALID_TITLE, actions.getSentMessage().get(0));
    }

    @Override
    String input() {
        return "download " + TITLE;
    }

    @Override
    void onSetup() {
        SpyStorageService storageService = new SpyStorageService();
        ClipManager clipManager = new ClipManagerImpl(storageService);
        clipManager.put(TEST_CLIP);
        clipManager.put(ANOTHER_TEST_CLIP);
        command = new DownloadClipCommand(clipManager);
        setInput(TITLE);
    }

    @Override
    String guild() {
        return GUILD;
    }

    private void fileWasSent(LocalClip clip) {
        assertEquals(1, actions.getSentFiles().size());
        assertEquals(clip.getPath(), actions.getSentFiles().get(0).getPath());
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("Sent file with name: " + clip.getTitle() + ".wav", actions.getSentMessage().get(0));
    }

    private void setInput(String clip) {
        actions.setUserInput("download " + clip);
        actions.setArg(DownloadClipCommand.ARG_CLIP, clip);
    }
}