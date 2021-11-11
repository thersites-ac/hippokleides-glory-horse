package net.picklepark.discord.service.impl;

import net.picklepark.discord.adaptor.SpyDiscordActions;
import net.picklepark.discord.annotation.Auth;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AuthServiceImplTest {

    private AuthServiceImpl authService;
    private boolean decision;
    private Auth.Level level;
    private SpyDiscordActions actions;

    @Before
    public void setAuthService() {
        authService = new AuthServiceImpl();
        actions = new SpyDiscordActions();
    }

    @Test
    public void anyAlwaysSucceeds() {
        givenLevel(Auth.Level.ANY);
        whenTestAuth();
        thenDecisionIsPass();
    }

    @Test
    public void ownerLevelAllowsGuildOwner() {
        givenLevel(Auth.Level.OWNER);
        givenUserIsOwner(42);
        whenTestAuth();
        thenDecisionIsPass();
    }

    @Test
    public void ownerLevelRejectsNonowner() {
        givenLevel(Auth.Level.OWNER);
        givenUserIsNotOwner(42);
        whenTestAuth();
        thenDecisionIsFail();
    }

    @Test
    public void channelOwnerIsAlsoAdmin() {
        givenLevel(Auth.Level.ADMIN);
        givenUserIsOwner(42);
        whenTestAuth();
        thenDecisionIsPass();
    }

    @Test
    public void canAddAdmins() {
        givenLevel(Auth.Level.ADMIN);
        givenUserIsNotOwner(42);
        givenAddAdmin(42);
        whenTestAuth();
        thenDecisionIsPass();
    }

    private void givenAddAdmin(long user) {
        actions.setGuildName("Guild");
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

    private void givenLevel(Auth.Level level) {
        this.level = level;
    }

    private void whenTestAuth() {
        decision = authService.isActionAuthorized(actions, level);
    }

    private void thenDecisionIsFail() {
        assertFalse(decision);
    }

    private void thenDecisionIsPass() {
        assertTrue(decision);
    }

}