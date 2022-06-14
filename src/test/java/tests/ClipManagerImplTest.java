package tests;

import net.picklepark.discord.command.audio.PlayClipCommand;
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
    private PlayClipCommand foundCommand;

    @Before
    public void setup() {
        clipManager = new ClipManagerImpl(null);
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
                .guild("baz")
                .build();
        clipManager.put(foo);
    }

    private void whenLookup() {
        foundCommand = clipManager.lookup("baz", "foo");
    }

    private void thenGetSameClip() {
        Assert.assertEquals("bar", foundCommand.getPath());
    }

}