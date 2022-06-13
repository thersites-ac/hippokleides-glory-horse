package tests;

import net.picklepark.discord.command.general.MakeAdminCommand;
import tools.SpyMessageReceivedActions;
import net.picklepark.discord.exception.DiscordCommandException;
import tools.SpyAuthManager;
import tools.TestConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MakeAdminCommandTest {

    private MakeAdminCommand makeAdminCommand;
    private SpyAuthManager spyAuthService;
    private SpyMessageReceivedActions actions;

    @Before
    public void setup() {
        actions = new SpyMessageReceivedActions();
        spyAuthService = new SpyAuthManager(new TestConfigService());
        makeAdminCommand = new MakeAdminCommand(spyAuthService);
        actions.setArg("username", "user");
        actions.setGuildName("guild");
    }

    @Test
    public void afterSuccessUserIsAdmin() throws DiscordCommandException {
        givenUserIsInGuild();
        whenMakeAdmin();
        thenUserIsAdmin();
    }

    @Test
    public void warnsIfUserIsNotChannelMember() throws DiscordCommandException {
        whenMakeAdmin();
        thenReceiveWarning();
    }

    private void givenUserIsInGuild() {
        actions.addGuildMember("user", 42);
    }

    private void whenMakeAdmin() throws DiscordCommandException {
        makeAdminCommand.execute(actions);
    }

    private void thenUserIsAdmin() {
        Set<Long> admins = spyAuthService.getAdminsFor("guild");
        assertTrue(admins.contains(42L));
    }

    private void thenReceiveWarning() {
        List<String> messages = actions.getSentMessage();
        assertEquals(1, messages.size());
        assertEquals("I can't find a user named user", messages.get(0));
    }

}