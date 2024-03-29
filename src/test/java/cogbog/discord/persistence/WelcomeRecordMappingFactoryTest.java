package cogbog.discord.persistence;

import cogbog.discord.model.LocalClip;
import cogbog.discord.model.WelcomeRecord;
import cogbog.discord.exception.DataMappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class WelcomeRecordMappingFactoryTest {

    private static final Long USER = 123L;
    private static final String GUILD = "234";
    private static final String CLIP_GUILD = GUILD;
    private static final String CLIP_PATH = "./foo";
    private static final String CLIP_TITLE = "foo";

    private static final Map<String, String> MAP = Map.of(
            WelcomeRecordMappingFactory.USER_ID, USER + "",
            WelcomeRecordMappingFactory.GUILD_ID, GUILD,
            WelcomeRecordMappingFactory.CLIP_GUILD, CLIP_GUILD,
            WelcomeRecordMappingFactory.CLIP_PATH, CLIP_PATH,
            WelcomeRecordMappingFactory.CLIP_TITLE, CLIP_TITLE
    );
    private static final LocalClip CLIP = LocalClip.builder()
            .guild(CLIP_GUILD)
            .path(CLIP_PATH)
            .title(CLIP_TITLE)
            .build();
    private static final WelcomeRecord RECORD = WelcomeRecord.builder()
            .guildId(GUILD)
            .userId(USER)
            .localClip(CLIP)
            .build();

    private WelcomeRecordMappingFactory factory;

    @Before
    public void setup() {
        factory = new WelcomeRecordMappingFactory("bar");
    }

    @Test
    public void fromMapHappyPath() {
        var result = factory.toMap(RECORD);
        assertEquals(MAP, result);
    }

    @Test
    public void toMapHappyPath() throws DataMappingException {
        var result = factory.fromMap(MAP);
        Assert.assertEquals(RECORD, result);
    }

    @Test(expected = DataMappingException.class)
    public void guildCannotBeNull() throws DataMappingException {
        fieldCannotBeNull(WelcomeRecordMappingFactory.GUILD_ID);
    }

    @Test(expected = DataMappingException.class)
    public void userCannotBeNull() throws DataMappingException {
        fieldCannotBeNull(WelcomeRecordMappingFactory.USER_ID);
    }

    @Test(expected = DataMappingException.class)
    public void guildCannotBeBlank() throws DataMappingException {
        fieldCannotEmpty(WelcomeRecordMappingFactory.GUILD_ID);
    }

    @Test(expected = DataMappingException.class)
    public void userCannotBeBlank() throws DataMappingException {
        fieldCannotEmpty(WelcomeRecordMappingFactory.USER_ID);
    }

    @Test(expected = DataMappingException.class)
    public void clipGuildCannotBeNull() throws DataMappingException {
        fieldCannotBeNull(WelcomeRecordMappingFactory.CLIP_GUILD);
    }

    @Test(expected = DataMappingException.class)
    public void clipGuildCannotBeEmpty() throws DataMappingException {
        fieldCannotEmpty(WelcomeRecordMappingFactory.CLIP_GUILD);
    }

    @Test(expected = DataMappingException.class)
    public void clipTitleCannotBeNull() throws DataMappingException {
        fieldCannotBeNull(WelcomeRecordMappingFactory.CLIP_TITLE);
    }

    @Test(expected = DataMappingException.class)
    public void clipTitleCannotBeEmpty() throws DataMappingException {
        fieldCannotEmpty(WelcomeRecordMappingFactory.CLIP_TITLE);
    }

    @Test(expected = DataMappingException.class)
    public void clipPathCannotBeNull() throws DataMappingException {
        fieldCannotBeNull(WelcomeRecordMappingFactory.CLIP_PATH);
    }

    @Test(expected = DataMappingException.class)
    public void clipPathCannotBeEmpty() throws DataMappingException {
        fieldCannotEmpty(WelcomeRecordMappingFactory.CLIP_PATH);
    }

    @Test(expected = DataMappingException.class)
    public void userIdMustBeValidLong() throws DataMappingException {
        var deviant = mapWith(WelcomeRecordMappingFactory.USER_ID, "abc");
        factory.fromMap(deviant);
    }

    @Test(expected = DataMappingException.class)
    public void userIdMustBePositive() throws DataMappingException {
        var deviant = mapWith(WelcomeRecordMappingFactory.USER_ID, "-1");
        factory.fromMap(deviant);
    }

    private void fieldCannotBeNull(String field) throws DataMappingException {
        var deviant = mapWith(field, null);
        factory.fromMap(deviant);
    }

    private void fieldCannotEmpty(String field) throws DataMappingException {
        var deviant = mapWith(field, "");
        factory.fromMap(deviant);
    }

    private Map<String, String> mapWith(String field, String value) {
        var result = new HashMap<>(MAP);
        result.put(field, value);
        return result;
    }
}