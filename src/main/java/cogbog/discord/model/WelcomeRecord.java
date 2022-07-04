package cogbog.discord.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class WelcomeRecord {
    String guildId;
    long userId;
    LocalClip localClip;
}
