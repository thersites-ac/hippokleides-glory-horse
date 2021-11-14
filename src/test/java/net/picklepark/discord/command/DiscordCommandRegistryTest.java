package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.annotation.UserInput;
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

    @Test
    public void skipsUnannotatedCommands() {
        givenUnannotatedCommand();
        whenUserInvokesWith("noannotation");
        thenSendsNothing();
    }

    private void givenUnannotatedCommand() {
        registry.register(new NoAuthAnnotationCommand());
    }

    private void givenUnauthorizedCommand() {
        authService.setAuthDecision(false);
    }

    private void givenAuthorizedCommand() {
        authService.setAuthDecision(true);
    }

    private void whenUserInvokesWith(String s) {
        actions.setUserInput("~" + s);
        registry.execute(actions);
    }

    private void thenTellsUserOff() {
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("Lol no fucking way", actions.getSentMessage().get(0));
    }

    private void thenSendsNothing() {
        assertTrue(actions.getSentMessage().isEmpty());
    }

    private void thenCommandExecutes() {
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("OK", actions.getSentMessage().get(0));
    }

    @UserInput("noannotation")
    private class NoAuthAnnotationCommand implements DiscordCommand {
        @Override
        public void execute(DiscordActions actions) throws DiscordCommandException {
        }
    }
}