package net.picklepark.discord.service.impl;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.general.NoopCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DynamicCommandManagerImplTest {

    private DynamicCommandManagerImpl dynamicCommandManager;
    private DiscordCommand command;
    private DiscordCommand foundCommand;

    @Before
    public void setup() {
        dynamicCommandManager = new DynamicCommandManagerImpl();
    }

    @Test
    public void canFetchAfterCreate() {
        givenCreateCommand();
        whenLookup();
        thenGetSameClip();
    }

    private void givenCreateCommand() {
        command = new NoopCommand();
        dynamicCommandManager.put("foo", command);
    }

    private void whenLookup() {
        foundCommand = dynamicCommandManager.lookup("foo");
    }

    private void thenGetSameClip() {
        Assert.assertEquals(command, foundCommand);
    }

}