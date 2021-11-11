package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.service.TestAuthService;
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
        whenUserInvokes();
        thenCommandExecutes();
    }

    @Test
    public void rejectsUnauthorizedCommand() {
        givenUnauthorizedCommand();
        whenUserInvokes();
        thenTellsUserOff();
    }

    private void givenUnauthorizedCommand() {
        authService.setAuthDecision(false);
    }

    private void givenAuthorizedCommand() {
        authService.setAuthDecision(true);
    }

    private void whenUserInvokes() {
        actions.setUserInput("~test");
        registry.execute(actions);
    }

    private void thenTellsUserOff() {
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("Lol no fucking way", actions.getSentMessage().get(0));
    }

    private void thenCommandExecutes() {
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("OK", actions.getSentMessage().get(0));
    }
}