package tests;

import net.picklepark.discord.command.audio.PlayClipCommand;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.impl.ClipManagerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class ClipManagerImplTest {

    private static final String TITLE = "title";
    private static final String PATH = "path";
    private static final String GUILD = "guild";
    private static final String SECOND_GUILD = "guild2";

    private ClipManagerImpl clipManager;

    @Before
    public void setup() {
        clipManager = new ClipManagerImpl(null);
    }

    @Test
    public void canFetchAfterCreate() {
        LocalClip foo = LocalClip.builder()
                .title(TITLE)
                .path(PATH)
                .guild(GUILD)
                .build();
        clipManager.put(foo);
        var foundCommand = clipManager.lookup(GUILD, TITLE);
        assertEquals(PATH, foundCommand.getPath());
    }

    @Test
    public void deletionFromOneGuildDoesNotAffectOthers() {
        clipManager.put(LocalClip.builder()
                .guild(GUILD)
                .path(PATH)
                .title(TITLE)
                .build());
        clipManager.put(LocalClip.builder()
                .guild(SECOND_GUILD)
                .path(PATH)
                .title(TITLE)
                .build());
        clipManager.delete(GUILD, TITLE);
        var firstResult = clipManager.lookup(GUILD, TITLE);
        var secondResult = clipManager.lookup(SECOND_GUILD, TITLE);
        assertNull(firstResult);
        assertEquals(PATH, secondResult.getPath());
    }
}