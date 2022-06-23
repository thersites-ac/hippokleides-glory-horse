package net.picklepark.discord.command.general;

import net.picklepark.discord.command.DiscordCommandRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.InMemoryAuthManager;
import tools.SpyMessageReceivedActions;
import tools.StubDiscordCommandRegistry;
import tools.TestCommand;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BanCommandTest {

    private DiscordCommandRegistry registry;
    private Long CHANNEL_OWNER = 42L;
    private Long VICTIM = 69L;
    private String BAN = "~ban " + VICTIM;
    private String TEST = "~test";
    private String GUILD = "guild";

    @Before
    public void setup() {
        var authManager = new InMemoryAuthManager();
        registry = new StubDiscordCommandRegistry(authManager);
        registry.prefix('~');
        registry.register(new TestCommand(), new BanCommand(authManager));
    }

    @Test
    public void beforeBanCanExecuteTestCommand() {
        var actions = actions(VICTIM);
        registry.execute(actions, TEST);
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("OK", actions.getSentMessage().get(0));
    }

    @Test
    public void sendsBanMessage() {
        var actions = actions(CHANNEL_OWNER);
        actions.setArg("user", VICTIM + "");
        registry.execute(actions, BAN);
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("You done goofed, " + VICTIM, actions.getSentMessage().get(0));
    }

    @Test
    public void afterBanVictimCannotCommandBot() {
        beforeBanCanExecuteTestCommand();
        sendsBanMessage();
        var actions = actions(VICTIM);
        registry.execute(actions, TEST);
        assertEquals(1, actions.getSentMessage().size());
        assertEquals("Lol no", actions.getSentMessage().get(0));
    }

    // TODO: more tests

    private SpyMessageReceivedActions actions(Long author) {
        var actions = new SpyMessageReceivedActions();
        actions.addGuildMember(VICTIM + "", VICTIM);
        actions.setAuthor(author);
        actions.setGuildName(GUILD);
        actions.setGuildOwner(CHANNEL_OWNER);
        return  actions;
    }
}