package net.picklepark.discord.command;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Catches;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.exception.NoSuchUserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DiscordCommandRegistryTests {

    private DiscordCommandRegistry registry;
    private boolean executed;
    private DiscordActions actions;
    private String userInput;
    private String sentMessage;
    private boolean failureWasHandled;

    @Before
    public void setup() {
        executed = false;
        failureWasHandled = false;
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
        givenRegistryWithPrefixAndCommand();
        whenReceiveMessage("test");
        thenExecutedCommand();
    }

    @Test
    public void usesSuccessMessage() throws Exception {
        givenRegistryWithPrefixAndCommand();
        whenReceiveMessage("test");
        thenSuccessMessageWasSent();
    }

    @Test
    public void usesFailureHandlers() throws Exception {
        givenRegistryWithPrefixAndFailureCommand();
        whenReceiveMessage("fail");
        thenFailureWasHandled();
    }

    private void thenFailureWasHandled() {
        Assert.assertTrue(failureWasHandled);
    }

    private void givenRegistryWithPrefixAndFailureCommand() {
        givenRegistry();
        givenSetPrefix();
        registry.register(new FailCommand());
    }

    private void thenSuccessMessageWasSent() {
        Assert.assertEquals(sentMessage, "OK");
    }

    private void givenRegistryWithPrefixAndCommand() {
        givenRegistry();
        givenRegisterCommand();
        givenSetPrefix();
    }

    private void givenRegistry() {
        registry = new DiscordCommandRegistry();
    }

    private void givenRegisterCommand() {
        registry.register(new TestCommand());
    }

    private void givenSetPrefix() {
        registry.prefix('~');
    }

    private void whenReceiveMessage(String message) throws Exception {
        userInput = "~" + message;
        registry.execute(actions);
    }

    private void thenExecutedCommand() {
        Assert.assertTrue(executed);
    }

    @UserInput("test")
    @SuccessMessage("OK")
    private class TestCommand implements DiscordCommand {
        @Override
        public void execute(DiscordActions actions) {
            executed = true;
        }
    }

    @UserInput("fail")
    @SuccessMessage("OK")
    private class FailCommand implements DiscordCommand {
        @Override
        public void execute(DiscordActions actions) throws NoSuchUserException {
            throw new NoSuchUserException("foo");
        }

        @Catches(NoSuchUserException.class)
        public void catchNoSuchUser(DiscordActions actions) {
            failureWasHandled = true;
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
    }

}
