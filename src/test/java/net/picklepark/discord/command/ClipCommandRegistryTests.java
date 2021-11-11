package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.service.TestAuthService;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ClipCommandRegistryTests {

    private DiscordCommandRegistry registry;
    private SpyDiscordActions actions;
    private Collection<DiscordCommand> registeredCommands;
    private SpyCommand testCommand;
    private DiscordCommand anotherTestCommand;
    private SpyCommand silentCommand;
    private TestAuthService rubberstampAuth;

    @Before
    public void setup() {
        rubberstampAuth = new TestAuthService();
        rubberstampAuth.setAuthDecision(true);
        actions = new SpyDiscordActions();
        testCommand = new TestCommand();
        anotherTestCommand = new AnotherTestCommand();
        silentCommand = new SilentCommand();
    }

    @Test
    public void canRegister() {
        givenRegistry();
        givenRegisterCommand();
    }

    @Test
    public void canSetPrefix() {
        givenRegistry();
        givenSetPrefix();
    }

    @Test
    public void usersUserInputAnnotation() {
        givenRegistryWithPrefixAndCommand(testCommand);
        whenReceiveMessage("test");
        thenExecutedCommand(testCommand);
    }

    @Test
    public void usesSuccessMessage() {
        givenRegistryWithPrefixAndCommand(testCommand);
        whenReceiveMessage("test");
        thenSuccessMessageWasSent();
    }

    @Test
    public void succeedsSilentlyIfNoSuccessAnnotationPresent() {
        givenRegistryWithPrefixAndCommand(silentCommand);
        whenReceiveMessage("silent");
        thenNoMessageWasSent();
    }

    @Test
    public void canRegisterAndExecuteSeveralCommands() {
        givenRegisterMultiple();
        whenReceiveMessages();
        thenBothWereReceived();
    }

    @Test
    public void canListRegistered() {
        givenRegisterMultiple();
        whenListCommands();
        thenListMatches();
    }

    private void givenRegisterMultiple() {
        givenRegistry();
        givenSetPrefix();
        registry.register(testCommand, anotherTestCommand);
    }

    private void givenRegistryWithPrefixAndCommand(DiscordCommand command) {
        givenRegistry();
        givenSetPrefix();
        registry.register(command);
    }

    private void givenRegistry() {
        registry = new DiscordCommandRegistry(new ClipManagerImpl(), rubberstampAuth);
    }

    private void givenRegisterCommand() {
        registry.register(testCommand);
    }

    private void givenSetPrefix() {
        registry.prefix('~');
    }

    private void whenListCommands() {
        registeredCommands = registry.getCommands();
    }

    private void whenReceiveMessages() {
        whenReceiveMessage("test");
        whenReceiveMessage("another test");
    }

    private void whenReceiveMessage(String message) {
        actions.setUserInput("~" + message);
        registry.execute(actions);
    }

    private void thenListMatches() {
        assertEquals(2, registeredCommands.size());
        assertTrue(registeredCommands.stream().anyMatch(command ->
                command instanceof TestCommand));
        assertTrue(registeredCommands.stream().anyMatch(command ->
                command instanceof AnotherTestCommand));
    }

    private void thenExecutedCommand(SpyCommand command) {
        assertTrue(command.isExecuted());
    }

    private void thenNoMessageWasSent() {
        thenExecutedCommand(silentCommand);
        assertTrue(actions.getSentMessage().isEmpty());
    }

    private void thenSuccessMessageWasSent() {
        thenExecutedCommand(testCommand);
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("OK", actions.getSentMessage().get(0));
    }

    private void thenBothWereReceived() {
        thenExecutedCommand(testCommand);
        assertEquals(2, actions.getSentMessage().size());
        assertEquals("OK", actions.getSentMessage().get(0));
        assertEquals("OK again", actions.getSentMessage().get(1));
    }

}
