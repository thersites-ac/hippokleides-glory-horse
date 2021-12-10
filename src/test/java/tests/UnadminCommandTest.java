package tests;

import net.picklepark.discord.command.general.UnadminCommand;
import tools.SpyDiscordActions;
import net.picklepark.discord.exception.DiscordCommandException;
import tools.TestAuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class UnadminCommandTest {

    private UnadminCommand unadminCommand;
    private TestAuthService authService;
    private SpyDiscordActions actions;

    @Before
    public void setup() {
        authService = new TestAuthService();
        unadminCommand = new UnadminCommand(authService);
        actions = new SpyDiscordActions();
    }

    @Test
    public void sendsDemotionMessage() throws DiscordCommandException {
        givenGuildAdmin("foo");
        whenDemote("foo");
        thenSendsMessage("You're fired, foo");
    }

    @Test
    public void notifiesOfUnidentifiableUser() throws DiscordCommandException {
        givenNonmember("foo");
        whenDemote("foo");
        thenSendsMessage("I don't know who foo is.");
    }

    @Test
    public void notifiesWhenUserIsNonadmin() throws DiscordCommandException {
        givenNonAdmin("foo");
        whenDemote("foo");
        thenSendsMessage("foo is already beneath my notice.");
    }

    private void givenNonAdmin(String username) {
        actions.addGuildMember(username, 42L);
        authService.throwAuthException();
    }

    private void givenNonmember(String username) {
        actions.setArg("user", username);
    }

    private void givenGuildAdmin(String username) {
        actions.addGuildMember(username, 42L);
    }

    private void whenDemote(String username) throws DiscordCommandException {
        actions.setArg("user", username);
        unadminCommand.execute(actions);
    }

    private void thenSendsMessage(String s) {
        assertEquals(s, actions.getSentMessage().get(0));
    }

}