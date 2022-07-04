package cogbog.discord.service.impl;

import cogbog.discord.model.LocalClip;
import cogbog.discord.adaptor.DataPersistenceAdaptor;
import cogbog.discord.model.WelcomeRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.InMemoryPersistenceAdaptor;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PersistenceWelcomeManagerImplTest {

    private static final long USER = 123L;
    private static final String GUILD = "234";
    private static final String TITLE = "foo";
    private static final String PATH = "path/";
    private static final LocalClip CLIP = LocalClip.builder()
            .title(TITLE)
            .path(PATH)
            .guild(GUILD)
            .build();

    private PersistenceWelcomeManagerImpl welcomeManager;
    private DataPersistenceAdaptor<WelcomeRecord> data;

    @Before
    public void setup() {
        data = InMemoryPersistenceAdaptor.forWelcomeRecords();
        welcomeManager = new PersistenceWelcomeManagerImpl(data);
    }

    @Test
    public void welcomeAfterSetWorks() throws IOException {
        welcomeManager.set(USER, GUILD, CLIP);
        var result = welcomeManager.welcome(USER, GUILD);
        Assert.assertEquals(CLIP, result);
    }

    @Test
    public void returnsNullIfWelcomeUnset() {
        var result = welcomeManager.welcome(USER, GUILD);
        assertNull(result);
    }

    @Test
    public void setTwiceOverrides() throws IOException {
        welcomeAfterSetWorks();
        var nextClip = LocalClip.builder()
                .guild(GUILD)
                .path("another/path")
                .title("another title")
                .build();
        welcomeManager.set(USER, GUILD, nextClip);
        var result = welcomeManager.welcome(USER, GUILD);
        Assert.assertEquals(nextClip, result);
    }

    @Test
    public void persistsAfterSet() throws IOException {
        welcomeManager.set(USER, GUILD, CLIP);
        welcomeManager = new PersistenceWelcomeManagerImpl(data);
        var result = welcomeManager.welcome(USER, GUILD);
        Assert.assertEquals(CLIP, result);
    }

    @Test
    public void readsFromRemoteWhenCacheEmpty() {
        var record = WelcomeRecord.builder()
                .localClip(CLIP)
                .userId(USER)
                .guildId(GUILD)
                .build();
        data.write(record);
        var result = welcomeManager.welcome(USER, GUILD);
        Assert.assertEquals(CLIP, result);
    }

    @Test
    public void returnsNullTwice() {
        returnsNullIfWelcomeUnset();
        returnsNullIfWelcomeUnset();
    }
}