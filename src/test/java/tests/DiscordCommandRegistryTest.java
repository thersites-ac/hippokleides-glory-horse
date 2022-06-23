package tests;

import net.picklepark.discord.command.DiscordCommandRegistry;
import tools.SpyMessageReceivedActions;
import net.picklepark.discord.command.general.IdkCommand;
import tools.StubDiscordCommandRegistry;
import tools.TestAuthManager;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import tools.TestCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

// fixme: combine this with CommandRegistryTests
@RunWith(JUnit4.class)
public class DiscordCommandRegistryTest {

    private DiscordCommandRegistry registry;
    private SpyMessageReceivedActions actions;
    private TestAuthManager authService;

    @Before
    public void setup() {
        authService = new TestAuthManager();
        registry = new StubDiscordCommandRegistry(authService);
        registry.prefix('~');
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
        authService.setAuthDecision(false);
    }

    private void givenAuthorizedCommand() {
        authService.setAuthDecision(true);
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