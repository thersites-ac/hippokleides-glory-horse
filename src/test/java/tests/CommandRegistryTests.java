package tests;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.command.DiscordCommandRegistry;
import tools.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;

import static org.junit.Assert.*;

// fixme: combine this with DiscordCommandRegistryTests
@RunWith(JUnit4.class)
public class CommandRegistryTests {

    private DiscordCommandRegistry registry;
    private SpyMessageReceivedActions actions;
    private Collection<DiscordCommand> registeredCommands;
    private SpyCommand testCommand;
    private DiscordCommand anotherTestCommand;
    private SpyCommand silentCommand;

    @Before
    public void setup() {
        actions = new SpyMessageReceivedActions();
        actions.setGuildName("guild");
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
    public void usesUserInputAnnotation() {
        givenRegistryWithCommand(testCommand);
        whenReceiveMessage("test");
        thenExecutedCommand(testCommand);
    }

    @Test
    public void usesSuccessMessage() {
        givenRegistryWithCommand(testCommand);
        whenReceiveMessage("test");
        thenSuccessMessageWasSent();
    }

    @Test
    public void succeedsSilentlyIfNoSuccessAnnotationPresent() {
        givenRegistryWithCommand(silentCommand);
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
        registry.register(testCommand, anotherTestCommand);
    }

    private void givenRegistryWithCommand(DiscordCommand command) {
        givenRegistry();
        registry.register(command);
    }

    private void givenRegistry() {
        registry = StubDiscordCommandRegistry.withRubberstampAuthManager();
    }

    private void givenRegisterCommand() {
        registry.register(testCommand);
    }

    private void whenListCommands() {
        registeredCommands = registry.getCommands();
    }

    private void whenReceiveMessages() {
        whenReceiveMessage("test");
        whenReceiveMessage("another test");
    }

    private void whenReceiveMessage(String message) {
        message = "~" + message;
        actions.setUserInput(message);
        registry.execute(actions, message);
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
