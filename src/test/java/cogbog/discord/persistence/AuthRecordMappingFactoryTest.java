package cogbog.discord.persistence;

import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.DataMappingException;
import cogbog.discord.model.AuthRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;

import static cogbog.discord.persistence.AuthRecordMappingFactory.GUILD_ID;
import static cogbog.discord.persistence.AuthRecordMappingFactory.USER_ID;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AuthRecordMappingFactoryTest {

    private static final String GUILD = "12345";
    private static final long USER = 987L;
    private static final String USER_STRING = USER + "";
    private static final AuthLevel LEVEL = AuthLevel.ADMIN;
    private static final String LEVEL_STRING = LEVEL.toString();
    private static final String LEVEL_KEY = "level";
    private static final AuthRecord RECORD = AuthRecord.builder()
            .level(LEVEL)
            .userId(USER)
            .guildId(GUILD)
            .build();

    private AuthRecordMappingFactory factory;

    @Before
    public void setup() {
        factory = new AuthRecordMappingFactory();
    }

    @Test
    public void translatesValidMapIntoAuthRecord() throws DataMappingException {
        var map = makeMap(GUILD, USER_STRING, LEVEL_STRING);
        var result = factory.fromMap(map);
        assertEquals(RECORD, result);
    }

    @Test(expected = DataMappingException.class)
    public void failsOnInvalidUserId() throws DataMappingException {
        var map = makeMap(GUILD, "user", LEVEL_STRING);
        factory.fromMap(map);
    }

    @Test(expected = DataMappingException.class)
    public void userIdMustBePositive() throws DataMappingException {
        var map = makeMap(GUILD, "-1", LEVEL_STRING);
        factory.fromMap(map);
    }

    @Test
    public void userIdCanBeZero() throws DataMappingException {
        var map = makeMap(GUILD, "0", LEVEL_STRING);
        var expected = AuthRecord.builder()
                .guildId(GUILD)
                .level(LEVEL)
                .userId(0L)
                .build();
        var result = factory.fromMap(map);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void userIdCanStartWith0() throws DataMappingException {
        var map = makeMap(GUILD, "01", LEVEL_STRING);
        var expected = AuthRecord.builder()
                .guildId(GUILD)
                .level(LEVEL)
                .userId(1L)
                .build();
        var result = factory.fromMap(map);
        Assert.assertEquals(expected, result);
    }

    private Map<String, String> makeMap(String guild, String user, String level) {
        return Map.of(
                GUILD_ID, guild,
                USER_ID, user,
                LEVEL_KEY, level
        );
    }

}