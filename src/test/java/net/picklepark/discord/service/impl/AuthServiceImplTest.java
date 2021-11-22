package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.service.AuthConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AuthServiceImplTest {

    private AuthServiceImpl authService;
    private boolean decision;
    private AuthLevel level;
    private SpyDiscordActions actions;
    private AuthConfigService testConfigService;

    @Before
    public void setup() {
        testConfigService = new TestConfigService();
        authService = new AuthServiceImpl(testConfigService);
        actions = new SpyDiscordActions();
        actions.setGuildName("Guild");
    }

    @Test
    public void anyAlwaysSucceeds() {
        givenLevel(AuthLevel.ANY);
        whenTestAuth();
        thenDecisionIsPass();
    }

    @Test
    public void ownerLevelAllowsGuildOwner() {
        givenLevel(AuthLevel.OWNER);
        givenUserIsOwner(42);
        whenTestAuth();
        thenDecisionIsPass();
    }

    @Test
    public void ownerLevelRejectsNonowner() {
        givenLevel(AuthLevel.OWNER);
        givenUserIsNotOwner(42);
        whenTestAuth();
        thenDecisionIsFail();
    }

    @Test
    public void channelOwnerIsAlsoAdmin() {
        givenLevel(AuthLevel.ADMIN);
        givenUserIsOwner(42);
        whenTestAuth();
        thenDecisionIsPass();
    }

    @Test
    public void canAddAdmins() {
        givenLevel(AuthLevel.ADMIN);
        givenUserIsNotOwner(42);
        givenAddAdmin(42);
        whenTestAuth();
        thenDecisionIsPass();
    }

    @Test
    public void authSettingsPersistAcrossInstances() {
        givenAddAdmin(42);
        whenRestart();
        thenUserIsAdmin(42);
    }

    private void givenAddAdmin(long user) {
        authService.addAdmin("Guild", user);
    }

    private void givenUserIsNotOwner(long user) {
        actions.setAuthor(user);
        actions.setGuildOwner(user + 1);
    }

    private void givenUserIsOwner(long user) {
        actions.setAuthor(user);
        actions.setGuildOwner(user);
    }

    private void givenLevel(AuthLevel level) {
        this.level = level;
    }

    private void whenTestAuth() {
        decision = authService.isActionAuthorized(actions, level);
    }

    private void whenRestart() {
        authService = new AuthServiceImpl(testConfigService);
    }

    private void thenUserIsAdmin(long id) {
        givenLevel(AuthLevel.ADMIN);
        actions.setAuthor(id);
        whenTestAuth();
        thenDecisionIsPass();
    }

    private void thenDecisionIsFail() {
        assertFalse(decision);
    }

    private void thenDecisionIsPass() {
        assertTrue(decision);
    }

}