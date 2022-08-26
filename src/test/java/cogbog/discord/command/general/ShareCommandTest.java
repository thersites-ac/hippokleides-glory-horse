package cogbog.discord.command.general;

import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.AuthLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.SpyMessageReceivedActions;

import static cogbog.discord.command.general.ShareCommand.SHARE_URL;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ShareCommandTest {

    private SpyMessageReceivedActions actions;
    private ShareCommand command;

    @Before
    public void setup() {
        command = new ShareCommand();
        actions = new SpyMessageReceivedActions();
    }

    @Test
    public void anyNonbannedUserCanShare() {
        assertEquals(command.requiredAuthLevel(), AuthLevel.USER);
    }

    @Test
    public void sendsMessage() throws DiscordCommandException {
        command.execute(actions);
        assertEquals(1, actions.getSentMessage().size());
        var message = actions.getSentMessage().get(0);
        assertTrue(message.contains(SHARE_URL));
        assertTrue(message.contains("Click here:"));
        assertTrue(message.contains("client_id="));
        assertTrue(message.contains("&permissions="));
        assertTrue(message.contains("&scope=bot"));
    }
}