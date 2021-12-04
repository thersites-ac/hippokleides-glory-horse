package net.picklepark.discord.tests;

import net.picklepark.discord.command.audio.ClipCommand;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ClipManagerImplTest {

    private ClipManagerImpl clipManager;
    private ClipCommand foundCommand;

    @Before
    public void setup() {
        clipManager = new ClipManagerImpl();
    }

    @Test
    public void canFetchAfterCreate() {
        givenCreateClip();
        whenLookup();
        thenGetSameClip();
    }

    private void givenCreateClip() {
        LocalClip foo = LocalClip.builder()
                .title("foo")
                .path("bar")
                .build();
        clipManager.put(foo);
    }

    private void whenLookup() {
        foundCommand = clipManager.lookup("foo");
    }

    private void thenGetSameClip() {
        Assert.assertEquals("bar", foundCommand.getPath());
    }

}