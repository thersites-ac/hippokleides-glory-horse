package cogbog.discord.persistence;

import cogbog.discord.exception.DataMappingException;
import cogbog.discord.model.LocalClip;
import cogbog.discord.model.WelcomeRecord;

import java.util.Map;

public class WelcomeRecordMappingFactory implements MappingFactory<WelcomeRecord> {

    public static final String USER_ID = "user_id";
    public static final String GUILD_ID = "guild_id";
    public static final String CLIP_GUILD = "clip_guild";
    public static final String CLIP_PATH = "clip_path";
    public static final String CLIP_TITLE = "clip_title";

    private static final String TABLE = "hippokleides_welcomes";

    @Override
    public Map<String, String> toMap(WelcomeRecord object) {
        return Map.of(
                USER_ID, object.getUserId() + "",
                GUILD_ID, object.getGuildId(),
                CLIP_GUILD, object.getLocalClip().getGuild(),
                CLIP_PATH, object.getLocalClip().getPath(),
                CLIP_TITLE, object.getLocalClip().getTitle()
        );
    }

    @Override
    public WelcomeRecord fromMap(Map<String, String> map) throws DataMappingException {
        var clipGuild = getValidMapEntry(map, CLIP_GUILD);
        var clipPath = getValidMapEntry(map, CLIP_PATH);
        var clipTitle = getValidMapEntry(map, CLIP_TITLE);
        var clip = LocalClip.builder()
                .guild(clipGuild)
                .path(clipPath)
                .title(clipTitle)
                .build();
        var guild = getValidMapEntry(map, GUILD_ID);
        var user = getValidMapEntry(map, USER_ID);
        try {
            long userId = Long.parseLong(user);
            if (userId < 0) {
                throw new DataMappingException(map);
            }
            return WelcomeRecord.builder()
                    .localClip(clip)
                    .userId(userId)
                    .guildId(guild)
                    .build();
        } catch (NumberFormatException e) {
            throw new DataMappingException(map, e);
        }
    }

    @Override
    public String getTable() {
        return TABLE;
    }

    private String getValidMapEntry(Map<String, String> map, String field) throws DataMappingException {
        var value = map.get(field);
        if (value == null || value.equals("")) {
            throw new DataMappingException(map);
        } else {
            return value;
        }
    }

}
