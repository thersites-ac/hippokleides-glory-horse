package net.picklepark.discord.service.impl;

import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;
import net.picklepark.discord.exception.NoOwnerException;
import net.picklepark.discord.model.AuthLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.SpyMessageReceivedActions;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PersistenceAuthManagerImplTest {

    private static final String GUILD = "guild";
    private static final long USER = 42L;
    private static final long OWNER = 100L;

    private SpyMessageReceivedActions actions;
    private PersistenceAuthManagerImpl authManager;

    @Before
    public void setup() {
        actions = new SpyMessageReceivedActions();
        actions.setAuthor(USER);
        actions.setGuildOwner(OWNER);
        actions.setGuildName(GUILD);
        authManager = new PersistenceAuthManagerImpl();
    }

    @Test
    public void defaultLevelIsUser() throws NoOwnerException {
        var result = whenCheckForLevel(AuthLevel.USER);
        assertTrue(result);
    }

    @Test
    public void userCannotPerformAdminTasks() throws NoOwnerException {
        var result = whenCheckForLevel(AuthLevel.ADMIN);
        assertFalse(result);
    }

    @Test
    public void userCannotPerformOwnerTasks() throws NoOwnerException {
        var result = whenCheckForLevel(AuthLevel.OWNER);
        assertFalse(result);
    }

    @Test
    public void adminCanPerformAdminTasks() throws IOException, AlreadyAdminException, NoOwnerException {
        givenPromote(USER);
        var result = whenCheckForLevel(AuthLevel.ADMIN);
        assertTrue(result);
    }

    @Test
    public void adminCannotPerformOwnerTasks() throws Exception {
        givenPromote(USER);
        var result = whenCheckForLevel(AuthLevel.OWNER);
        assertFalse(result);
    }

    @Test
    public void adminCanPerformUserTasks() throws Exception {
        givenPromote(USER);
        var result = whenCheckForLevel(AuthLevel.USER);
        assertTrue(result);
    }

    @Test
    public void demotedAdminIsUser() throws Exception {
        givenPromote(USER);
        givenDemote(USER);
        var adminResult = whenCheckForLevel(AuthLevel.ADMIN);
        var userResult = whenCheckForLevel(AuthLevel.USER);
        assertFalse(adminResult);
        assertTrue(userResult);
    }

    @Test
    public void canOnlyDemoteAdmins() {
        fail();
    }

    @Test
    public void ownerCanDoEverything() throws Exception {
        actions.setAuthor(OWNER);
        var ownerResult = whenCheckForLevel(AuthLevel.OWNER);
        var adminResult = whenCheckForLevel(AuthLevel.ADMIN);
        var userResult = whenCheckForLevel(AuthLevel.USER);
        assertTrue(ownerResult);
        assertTrue(adminResult);
        assertTrue(userResult);
    }

    @Test
    public void loseUserPrivilegesAfterBan() {
        fail();
    }

    @Test
    public void unbanReversesBan() {
        fail();
    }

    @Test
    public void canOnlyUnbanBannedMembers() {
        fail();
    }

    @Test
    public void persistsData() {
        fail("This needs to be several different tests");
    }

    private void givenPromote(long user) throws IOException, AlreadyAdminException {
        authManager.addAdmin(GUILD, user);
    }

    private void givenDemote(long user) throws IOException, AuthException {
        authManager.demote(user, actions);
    }

    private boolean whenCheckForLevel(AuthLevel level) throws NoOwnerException {
        return authManager.isActionAuthorized(actions, level);
    }
}
