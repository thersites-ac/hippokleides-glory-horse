package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.command.general.NoopCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.impl.TestAuthService;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class DiscordCommandRegistryTest {

    private DiscordCommandRegistry registry;
    private SpyDiscordActions actions;
    private TestAuthService authService;

    @Before
    public void setup() {
        authService = new TestAuthService();
        registry = new DiscordCommandRegistry(new ClipManagerImpl(), authService);
        registry.prefix('~');
        registry.register(new TestCommand());
        actions = new SpyDiscordActions();
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
        registry.register(new NoopCommand());
    }

    private void whenUserInvokesWith(String s) {
        actions.setUserInput("~" + s);
        registry.execute(actions);
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