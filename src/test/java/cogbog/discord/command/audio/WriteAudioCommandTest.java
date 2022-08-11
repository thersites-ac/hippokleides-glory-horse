package cogbog.discord.command.audio;

import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.service.RecordingService;
import cogbog.discord.service.UrlShortener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.MockRecordingService;
import tools.PassthroughUrlShortener;
import tools.SpyMessageReceivedActions;
import tools.SpyStorageService;

import java.net.URLEncoder;
import java.nio.charset.Charset;

import static cogbog.discord.command.audio.WriteAudioCommand.USERNAME;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class WriteAudioCommandTest {

    private static final String PREFIX = "~";
    private static final String PREFIX_ENCODED = URLEncoder.encode(PREFIX, Charset.defaultCharset());
    private static final String USER = "user";
    private static final String RECORDING_ID = "42";

    private WriteAudioCommand command;
    private SpyMessageReceivedActions actions;
    private RecordingService recordingService;
    private SpyStorageService storageService;
    private UrlShortener urlShortener;

    @Before
    public void setup() {
        recordingService = new MockRecordingService();
        storageService = new SpyStorageService(RECORDING_ID);
        urlShortener = new PassthroughUrlShortener();
        command = new WriteAudioCommand(recordingService, storageService, urlShortener, PREFIX);
        actions = new SpyMessageReceivedActions();
        actions.setArg(USERNAME, USER);
        actions.addGuildMember(USER, 69L);
    }

    @Test
    public void sendsCropLink() throws DiscordCommandException {
        var message = executeSuccessfully();
        assertTrue(message.matches(".*http://.*"));
    }

    @Test
    public void includesGuildPrefixParam() throws DiscordCommandException {
        var message = executeSuccessfully();
        assertTrue(message.contains("guild_prefix=" + PREFIX_ENCODED));
    }

    @Test
    public void includesRecordingIdParam() throws DiscordCommandException {
        var message = executeSuccessfully();
        assertTrue(message.contains("recording_id=" + RECORDING_ID));
    }

    private String executeSuccessfully() throws DiscordCommandException {
        command.execute(actions);
        assertEquals(1, actions.getSentMessage().size());
        var message = actions.getSentMessage().get(0);
        assertTrue(message.contains("OK, now go to"));
        return message;
    }
}