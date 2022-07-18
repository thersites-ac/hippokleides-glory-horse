package cogbog.discord.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ClipMetadata {
    long originatingTextChannel;
    long recordedUser;
    long creator;
}
