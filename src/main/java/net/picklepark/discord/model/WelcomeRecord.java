package net.picklepark.discord.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class WelcomeRecord {

    // fixme: move to the mapping factory when I make that
    public static final String USER_ID = "user_id";
    public static final String GUILD_ID = "guild_id";

    String guildId;
    String userId;
    LocalClip localClip;
}
