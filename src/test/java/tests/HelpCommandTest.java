package tests;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.command.DiscordCommandRegistry;
import cogbog.discord.command.general.HelpCommand;
import cogbog.discord.command.general.ShareCommand;
import tools.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class HelpCommandTest {

    private DiscordCommandRegistry registry;
    private SpyCommand testCommand;
    private SpyMessageReceivedActions actions;
    private DiscordCommand anotherCommand;
    private DiscordCommand helpCommand;

    @Before
    public void setup() {
        registry = StubDiscordCommandRegistry.withRubberstampAuthManager();
        testCommand = new TestCommand();
        anotherCommand = new AnotherTestCommand();
        actions = new SpyMessageReceivedActions();
        actions.setGuildName("guild");
        helpCommand = new HelpCommand(registry);
    }

    @Test
    public void readsCommandsFromRegistry() {
        givenRegistryContainsTestCommands();
        whenRunHelp();
        thenSentAllRegisteredHelpMessages();
    }

    @Test
    public void alsoAddsShareMessage() {
        givenRegistryContainsTestCommands();
        whenRunHelp();
        thenAlsoSentShareMessage();
    }

    private void thenAlsoSentShareMessage() {
        assertTrue(actions.getSentMessage().size() >= 2);
        var message = actions.getSentMessage().get(1);
        assertTrue(message.contains("add me to your server"));
        assertTrue(message.contains(ShareCommand.SHARE_URL));
    }

    private void givenRegistryContainsTestCommands() {
        registry.register(helpCommand);
        registry.register(testCommand);
        registry.register(anotherCommand);
    }

    private void whenRunHelp() {
        String message = "~help";
        actions.setUserInput(message);
        registry.execute(actions, message);
    }

    private void thenSentAllRegisteredHelpMessages() {
        List<String> sentMessages = actions.getSentMessage();
        assertFalse(sentMessages.isEmpty());
        String helpMessage = sentMessages.get(0);
        assertTrue(helpMessage.contains("halp"));
        assertTrue(helpMessage.contains("plz help"));
    }
}