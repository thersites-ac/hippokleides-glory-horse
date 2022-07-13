package tests;

import cogbog.discord.command.DiscordCommandRegistry;
import tools.SpyMessageReceivedActions;
import cogbog.discord.command.general.IdkCommand;
import tools.StubDiscordCommandRegistry;
import tools.TestAuthManager;
import tools.TestCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

// fixme: combine this with CommandRegistryTests
@RunWith(JUnit4.class)
public class DiscordCommandRegistryAuthorizationTest {

    private DiscordCommandRegistry registry;
    private SpyMessageReceivedActions actions;
    private TestAuthManager authManager;

    @Before
    public void setup() {
        authManager = new TestAuthManager();
        registry = new StubDiscordCommandRegistry(authManager);
        registry.register(new TestCommand());
        actions = new SpyMessageReceivedActions();
        actions.setGuildName("guild");
    }

    @Test
    public void acceptsAuthorizedCommands() {
        givenAuthorizedCommand();
        whenUserInvokesWith("test");
        thenCommandExecutes();
    }

    @Test
    public void rejectsUnauthorizedCommand() {
        givenUnauthorizedCommand();
        whenUserInvokesWith("test");
        thenTellsUserOff();
    }

    @Test(expected = RuntimeException.class)
    public void cannotRegisterNoop() {
        whenRegisterNoop();
    }

    private void givenUnauthorizedCommand() {
        authManager.setAuthDecision(false);
    }

    private void givenAuthorizedCommand() {
        authManager.setAuthDecision(true);
    }

    private void whenRegisterNoop() {
        registry.register(new IdkCommand());
    }

    private void whenUserInvokesWith(String s) {
        s = "~" + s;
        actions.setUserInput(s);
        registry.execute(actions, s);
    }

    private void thenTellsUserOff() {
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("Lol no", actions.getSentMessage().get(0));
    }

    private void thenCommandExecutes() {
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("OK", actions.getSentMessage().get(0));
    }

}