package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.model.AuthLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.SpyMessageReceivedActions;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PersistenceAuthManagerImplTest {

    private static final String GUILD = "guild";

    private SpyMessageReceivedActions actions;
    private PersistenceAuthManagerImpl authManager;

    @Before
    public void setup() {
        actions = new SpyMessageReceivedActions();
        actions.setAuthor(42L);
        actions.setGuildOwner(100L);
        actions.setGuildName(GUILD);
        authManager = new PersistenceAuthManagerImpl();
    }

    @Test
    public void defaultLevelIsUser() {
        var result = whenCheckForLevel(AuthLevel.USER);
        assertTrue(result);
    }

    @Test
    public void userCannotPerformAdminTasks() {
        var result = whenCheckForLevel(AuthLevel.ADMIN);
        assertFalse(result);
    }

    private boolean whenCheckForLevel(AuthLevel level) {
        return authManager.isActionAuthorized(actions, level);
    }
}
