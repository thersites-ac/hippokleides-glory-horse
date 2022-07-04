package cogbog.discord.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class AuthRecord {
    String guildId;
    long userId;
    AuthLevel level;
}
