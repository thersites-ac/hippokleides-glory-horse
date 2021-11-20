package net.picklepark.discord.adaptor.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class JdaDiscordActionsTest {

    private JdaDiscordActions jdaDiscordActions;

    @Before
    public void setup() {
        jdaDiscordActions = new JdaDiscordActions(null, null);
    }

    @Test
    public void userRegexRequiresExactlyFourDigitsAfterPoundsign() {
        assertTrue(jdaDiscordActions.isTag("someuser#1234"));
        assertTrue(jdaDiscordActions.isTag("someUser#0123"));
        assertTrue(jdaDiscordActions.isTag("***some_user!#0987"));
        assertFalse(jdaDiscordActions.isTag("someuser1234"));
        assertFalse(jdaDiscordActions.isTag("someUser#12345"));
        assertFalse(jdaDiscordActions.isTag("***some_user!#123"));
    }

}