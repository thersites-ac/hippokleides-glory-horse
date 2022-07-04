package cogbog.discord.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class LocalClip implements Serializable {

    private static final long serialVersionUid = 1L;
    private final String path;
    private final String title;
    private final String guild;
}
