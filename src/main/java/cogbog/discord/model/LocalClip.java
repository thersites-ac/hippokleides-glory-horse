package cogbog.discord.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class LocalClip implements Serializable {
    String path;
    String title;
    String guild;
    ClipMetadata metadata;
}
