package net.picklepark.discord.command;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.exception.NoSuchUserException;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.impl.DynamicCommandManagerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@RunWith(JUnit4.class)
public class DiscordCommandRegistryTests {

    private DiscordCommandRegistry registry;
    private boolean executed;
    private DiscordActions actions;
    private String userInput;
    private String sentMessage;

    @Before
    public void setup() {
        sentMessage = "init";
        executed = false;
        actions = new TestDiscordActions();
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
        givenRegistryWithPrefixAndCommand(new TestCommand());
        whenReceiveMessage("test");
        thenExecutedCommand();
    }

    @Test
    public void usesSuccessMessage() throws Exception {
        givenRegistryWithPrefixAndCommand(new TestCommand());
        whenReceiveMessage("test");
        thenSuccessMessageWasSent();
    }

    @Test
    public void succeedsSilentlyIfNoSuccessAnnotationPresent() throws Exception {
        givenRegistryWithPrefixAndCommand(new SilentCommand());
        whenReceiveMessage("silent");
        thenNoMessageWasSent();
    }

    @Test
    public void canRegisterAndExecuteSeveralCommands() throws Exception {
        givenRegisterMultiple();
        whenReceiveMessages();
        thenBothWereExecuted();
    }

    private void givenRegisterMultiple() {
        givenRegistry();
        givenSetPrefix();
        registry.register(new TestCommand(), new AnotherTestCommand());
    }

    private void givenRegistryWithPrefixAndCommand(DiscordCommand command) {
        givenRegistry();
        givenSetPrefix();
        registry.register(command);
    }

    private void givenRegistry() {
        registry = new DiscordCommandRegistry(new TestStorageService(), new DynamicCommandManagerImpl());
    }

    private void givenRegisterCommand() {
        registry.register(new TestCommand());
    }

    private void givenSetPrefix() {
        registry.prefix('~');
    }

    private void whenReceiveMessages() throws Exception {
        whenReceiveMessage("another test");
        whenReceiveMessage("test");
    }

    private void whenReceiveMessage(String message) throws Exception {
        userInput = "~" + message;
        registry.execute(actions);
    }

    private void thenExecutedCommand() {
        Assert.assertTrue(executed);
    }

    private void thenNoMessageWasSent() {
        thenExecutedCommand();
        Assert.assertEquals("init", sentMessage);
    }

    private void thenSuccessMessageWasSent() {
        thenExecutedCommand();
        Assert.assertEquals(sentMessage, "OK");
    }

    private void thenBothWereExecuted() {
        thenSuccessMessageWasSent();
    }

    @UserInput("silent")
    private class SilentCommand implements DiscordCommand {
        @Override
        public void execute(DiscordActions actions) {
            executed = true;
        }
    }

    @UserInput("test")
    @SuccessMessage("OK")
    private class TestCommand implements DiscordCommand {
        @Override
        public void execute(DiscordActions actions) {
            executed = true;
        }
    }

    @UserInput("other")
    @SuccessMessage("OK again")
    private class AnotherTestCommand implements DiscordCommand {
        @Override
        public void execute(DiscordActions actions) {
        }
    }

    private class TestDiscordActions implements DiscordActions {
        @Override
        public void send(String message) {
            sentMessage = message;
        }
        @Override
        public void send(MessageEmbed embed) {
        }
        @Override
        public void setReceivingHandler(AudioReceiveHandler handler) {
        }
        @Override
        public void connect() {
        }
        @Override
        public User lookupUser(String user) throws NoSuchUserException {
            return null;
        }
        @Override
        public String userInput() {
            return userInput;
        }
        @Override
        public String getArgument(String arg) {
            return null;
        }
        @Override
        public void setVolume(int volume) {
        }
        @Override
        public void disconnect() {
        }
        @Override
        public int getVolume() {
            return 0;
        }
        @Override
        public void pause() {
        }
        @Override
        public void unpause() {
        }
        @Override
        public void skip() {
        }
        @Override
        public void queue(String uri) {
        }

        @Override
        public void initMatches(String regex, String message) {
        }
    }

    private class TestStorageService implements StorageService {
        @Override
        public Coordinates store(File file) throws MalformedURLException {
            return null;
        }

        @Override
        public LocalClip download(String objectKey) throws URISyntaxException, ResourceNotFoundException {
            return null;
        }
    }

}
