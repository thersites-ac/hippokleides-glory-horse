package cogbog.discord.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.net.URL;

@Builder
@Value
public class Recording {
    URL recordingUri;
    String recordingId;
    String key;
    String prefix;
    URL waveformUri;
}
