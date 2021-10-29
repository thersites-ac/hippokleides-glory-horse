package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.RemoteStorageService;
import net.picklepark.discord.service.impl.DynamicCommandManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class DiscordCommandRegistryTests {

    private DiscordCommandRegistry registry;
    private SpyDiscordActions actions;
    private Collection<DiscordCommand> registeredCommands;
    private SpyCommand testCommand;
    private DiscordCommand anotherTestCommand;
    private SpyCommand silentCommand;

    @Before
    public void setup() {
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
    public void usersUserInputAnnotation() throws Exception {
        givenRegistryWithPrefixAndCommand(testCommand);
        whenReceiveMessage("test");
        thenExecutedCommand(testCommand);
    }

    @Test
    public void usesSuccessMessage() throws Exception {
        givenRegistryWithPrefixAndCommand(testCommand);
        whenReceiveMessage("test");
        thenSuccessMessageWasSent();
    }

    @Test
    public void succeedsSilentlyIfNoSuccessAnnotationPresent() throws Exception {
        givenRegistryWithPrefixAndCommand(silentCommand);
        whenReceiveMessage("silent");
        thenNoMessageWasSent();
    }

    @Test
    public void canRegisterAndExecuteSeveralCommands() throws Exception {
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
        registry = new DiscordCommandRegistry(new TestRemoteStorageService(), new DynamicCommandManagerImpl());
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

    private void whenReceiveMessages() throws Exception {
        whenReceiveMessage("test");
        whenReceiveMessage("another test");
    }

    private void whenReceiveMessage(String message) throws Exception {
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

    private class TestRemoteStorageService implements RemoteStorageService {
        @Override
        public Coordinates store(File file) throws MalformedURLException {
            return null;
        }

        @Override
        public LocalClip download(String objectKey) throws URISyntaxException, ResourceNotFoundException {
            return null;
        }

        @Override
        public void sync() {
        }

        @Override
        public void delete(String key) {
        }
    }

}
