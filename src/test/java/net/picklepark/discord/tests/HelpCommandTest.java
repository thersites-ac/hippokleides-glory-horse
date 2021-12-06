package net.picklepark.discord.tests;

import net.picklepark.discord.command.general.HelpCommand;
import net.picklepark.discord.tools.AnotherTestCommand;
import net.picklepark.discord.tools.SpyCommand;
import net.picklepark.discord.tools.SpyDiscordActions;
import net.picklepark.discord.tools.RubberstampAuthService;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import net.picklepark.discord.tools.TestCommand;
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
    private SpyDiscordActions actions;
    private DiscordCommand anotherCommand;
    private DiscordCommand helpCommand;

    @Before
    public void setup() {
        registry = new DiscordCommandRegistry(new ClipManagerImpl(), new RubberstampAuthService());
        testCommand = new TestCommand();
        anotherCommand = new AnotherTestCommand();
        actions = new SpyDiscordActions();
        helpCommand = new HelpCommand(registry);
        registry.prefix('~');
    }

    @Test
    public void readsCommandsFromRegistry() {
        givenRegistryContainsTestCommands();
        whenRunHelp();
        thenSentAllRegisteredHelpMessages();
    }

    private void givenRegistryContainsTestCommands() {
        registry.register(helpCommand);
        registry.register(testCommand);
        registry.register(anotherCommand);
    }

    private void whenRunHelp() {
        actions.setUserInput("~help");
        registry.execute(actions);
    }

    private void thenSentAllRegisteredHelpMessages() {
        List<String> sentMessages = actions.getSentMessage();
        assertFalse(sentMessages.isEmpty());
        String helpMessage = sentMessages.get(0);
        assertTrue(helpMessage.contains("halp"));
        assertTrue(helpMessage.contains("plz help"));
    }

}