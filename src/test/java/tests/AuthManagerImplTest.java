package tests;

import cogbog.discord.exception.AlreadyAdminException;
import cogbog.discord.exception.AuthException;
import cogbog.discord.exception.CannotDemoteSelfException;
import cogbog.discord.service.impl.AuthManagerImpl;
import tools.InMemoryBanPersister;
import tools.SpyMessageReceivedActions;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.AuthLevelConflictException;
import cogbog.discord.service.AuthConfigService;
import tools.TestConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AuthManagerImplTest {

    private static final String GUILD_NAME = "Guild";

    private AuthManagerImpl authService;
    private boolean decision;
    private AuthLevel level;
    private SpyMessageReceivedActions actions;
    private AuthConfigService testConfigService;

    @Before
    public void setup() {
        var banPersister = new InMemoryBanPersister();
        banPersister.reset();
        testConfigService = new TestConfigService();
        authService = new AuthManagerImpl(testConfigService, banPersister);
        actions = new SpyMessageReceivedActions();
        actions.setGuildName(GUILD_NAME);
    }

    @Test
    public void anyAlwaysSucceeds() {
        givenLevel(AuthLevel.USER);
        whenTestAuthFor(42);
        thenDecisionIsPass();
    }

    @Test
    public void ownerLevelAllowsGuildOwner() {
        givenLevel(AuthLevel.OWNER);
        givenUserIsOwner(42);
        whenTestAuthFor(42);
        thenDecisionIsPass();
    }

    @Test
    public void ownerLevelRejectsNonowner() {
        givenLevel(AuthLevel.OWNER);
        givenUserIsNotOwner(42);
        whenTestAuthFor(42);
        thenDecisionIsFail();
    }

    @Test
    public void channelOwnerIsAlsoAdmin() {
        givenLevel(AuthLevel.ADMIN);
        givenUserIsOwner(42);
        whenTestAuthFor(42);
        thenDecisionIsPass();
    }

    @Test
    public void canAddAdmins() throws IOException, AlreadyAdminException {
        givenLevel(AuthLevel.ADMIN);
        givenUserIsNotOwner(42);
        givenAddAdmin(42);
        whenTestAuthFor(42);
        thenDecisionIsPass();
    }

    @Test
    public void authSettingsPersistAcrossInstances() throws IOException, AlreadyAdminException {
        givenAddAdmin(42);
        whenRestart();
        thenUserIsAdmin(42);
    }

    @Test
    public void demotedUserIsNotAdmin() throws AuthException, IOException, AlreadyAdminException {
        givenLevel(AuthLevel.ADMIN);
        givenAddAdmin(42);
        whenDemote(42);
        whenTestAuthFor(42);
        thenDecisionIsFail();
    }

    @Test(expected = AuthLevelConflictException.class)
    public void cannotDemotePeon() throws AuthException, IOException {
        whenDemote(42);
    }

    @Test(expected = CannotDemoteSelfException.class)
    public void cannotDemoteGuildOwner() throws AuthException, IOException {
        givenUserIsOwner(42);
        whenDemote(42);
    }

    @Test
    public void afterBanNothingIsAuthorized() throws IOException {
        var user = 42;
        givenUserIsNotOwner(user);
        givenLevel(AuthLevel.USER);
        whenTestAuthFor(user);
        thenDecisionIsPass();

        givenBan(user);
        thenUserIsBanned(user);
    }

    @Test
    public void persistsBans() throws IOException {
        var user = 42;
        givenBan(user);
        whenRestart();
        thenUserIsBanned(42);
    }

    // TODO: more tests for ban

    private void givenAddAdmin(long user) throws IOException, AlreadyAdminException {
        authService.addAdmin(GUILD_NAME, user);
    }

    private void givenBan(int user) throws IOException {
        authService.ban(GUILD_NAME, user);
    }

    private void givenUserIsNotOwner(long user) {
        actions.setGuildOwner(user + 1);
    }

    private void givenUserIsOwner(long user) {
        actions.setGuildOwner(user);
    }

    private void givenLevel(AuthLevel level) {
        this.level = level;
    }

    private void whenDemote(int user) throws AuthException, IOException {
        actions.setGuildName(GUILD_NAME);
        authService.demote(user, actions);
    }

    private void whenTestAuthFor(long user) {
        actions.setAuthor(user);
        decision = authService.isActionAuthorized(actions, level);
    }

    private void whenRestart() {
        authService = new AuthManagerImpl(testConfigService, new InMemoryBanPersister());
    }

    private void thenUserIsAdmin(long id) {
        givenLevel(AuthLevel.ADMIN);
        whenTestAuthFor(id);
        thenDecisionIsPass();
    }

    private void thenDecisionIsFail() {
        assertFalse(decision);
    }

    private void thenDecisionIsPass() {
        assertTrue(decision);
    }

    private void thenUserIsBanned(int user) {
        whenTestAuthFor(user);
        thenDecisionIsFail();
    }
}