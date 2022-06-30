package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.DataPersistenceAdaptor;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.AuthException;
import net.picklepark.discord.exception.AuthLevelConflictException;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.model.AuthRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.InMemoryPersistenceAdaptor;
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
    private DataPersistenceAdaptor<AuthRecord> data;

    @Before
    public void setup() {
        actions = new SpyMessageReceivedActions();
        actions.setAuthor(USER);
        actions.setGuildOwner(OWNER);
        actions.setGuildName(GUILD);
        data = InMemoryPersistenceAdaptor.forAuthRecords();
        authManager = new PersistenceAuthManagerImpl(data);
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

    @Test
    public void userCannotPerformOwnerTasks() {
        var result = whenCheckForLevel(AuthLevel.OWNER);
        assertFalse(result);
    }

    @Test
    public void adminCanPerformAdminTasks() throws IOException, AlreadyAdminException {
        givenPromote();
        var result = whenCheckForLevel(AuthLevel.ADMIN);
        assertTrue(result);
    }

    @Test
    public void adminCannotPerformOwnerTasks() throws Exception {
        givenPromote();
        var result = whenCheckForLevel(AuthLevel.OWNER);
        assertFalse(result);
    }

    @Test
    public void adminCanPerformUserTasks() throws Exception {
        givenPromote();
        var result = whenCheckForLevel(AuthLevel.USER);
        assertTrue(result);
    }

    @Test
    public void demotedAdminIsUser() throws Exception {
        givenPromote();
        givenDemote();
        var adminResult = whenCheckForLevel(AuthLevel.ADMIN);
        var userResult = whenCheckForLevel(AuthLevel.USER);
        assertFalse(adminResult);
        assertTrue(userResult);
    }

    @Test(expected = AuthLevelConflictException.class)
    public void canOnlyDemoteAdmins() throws IOException, AuthException {
        givenDemote();
    }

    @Test
    public void ownerCanDoEverything() {
        actions.setAuthor(OWNER);
        var ownerResult = whenCheckForLevel(AuthLevel.OWNER);
        var adminResult = whenCheckForLevel(AuthLevel.ADMIN);
        var userResult = whenCheckForLevel(AuthLevel.USER);
        assertTrue(ownerResult);
        assertTrue(adminResult);
        assertTrue(userResult);
    }

    @Test
    public void loseUserPrivilegesAfterBan() throws IOException {
        givenBan();
        var userResult = whenCheckForLevel(AuthLevel.USER);
        var banResult = whenCheckForLevel(AuthLevel.BANNED);
        assertFalse(userResult);
        assertTrue(banResult);
    }

    @Test
    public void unbanReversesBan() throws IOException, AuthException {
        givenBan();
        givenUnban();
        var adminResult = whenCheckForLevel(AuthLevel.ADMIN);
        var userResult = whenCheckForLevel(AuthLevel.USER);
        assertFalse(adminResult);
        assertTrue(userResult);
    }

    @Test(expected = AuthLevelConflictException.class)
    public void canOnlyUnbanBannedMembers() throws IOException, AuthException {
        givenUnban();
    }

    @Test
    public void persistsAdmins() throws IOException, AlreadyAdminException {
        givenPromote();
        givenRestart();
        var result = whenCheckForLevel(AuthLevel.ADMIN);
        assertTrue(result);
    }

    @Test
    public void persistsBans() throws IOException {
        givenBan();
        givenRestart();
        var result = whenCheckForLevel(AuthLevel.BANNED);
        assertTrue(result);
    }

    private void givenPromote() throws IOException, AlreadyAdminException {
        authManager.addAdmin(GUILD, USER);
    }

    private void givenDemote() throws IOException, AuthException {
        authManager.demote(USER, actions);
    }

    private void givenBan() throws IOException {
        authManager.ban(GUILD, USER);
    }

    private void givenUnban() throws IOException, AuthException {
        authManager.unban(GUILD, USER);
    }

    private void givenRestart() {
        authManager = new PersistenceAuthManagerImpl(data);
    }

    private boolean whenCheckForLevel(AuthLevel level) {
        return authManager.isActionAuthorized(actions, level);
    }
}
