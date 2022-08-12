package cogbog.discord.model;

import lombok.Builder;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

import java.util.Map;

@Builder
@Value
public class ClipMetadata {

    public static final String CREATOR = "creator";
    public static final String ORIGINATING_TEXT_CHANNEL = "originating-text-channel";
    public static final String RECORDED_USER = "recorded-user";
    public static final String RECORDING_ID = "recording_id";

    private static final Logger logger = LoggerFactory.getLogger(ClipMetadata.class);

    long originatingTextChannel;
    long recordedUser;
    long creator;
    String recordingId;

    public static ClipMetadata fromMap(Map<String, String> tags) {
        long creator = parseLong(tags, CREATOR);
        long originatingTextChannel = parseLong(tags, ORIGINATING_TEXT_CHANNEL);
        long recordedUser = parseLong(tags, RECORDED_USER);
        return ClipMetadata.builder()
                .creator(creator)
                .originatingTextChannel(originatingTextChannel)
                .recordedUser(recordedUser)
                .recordingId(tags.get(RECORDING_ID))
                .build();
    }

    private static long parseLong(Map<String, String> tags, String key) {
        try {
            String result = tags.get(key);
            return Long.parseLong(result);
        } catch (NumberFormatException e) {
            logger.warn("Cannot construct full clip metadata from map: " + tags.toString());
            return -1;
        }
    }

    public Tagging toTagging() {
        return Tagging.builder()
                .tagSet(Tag.builder().key(CREATOR).value(creator + "").build(),
                        Tag.builder().key(RECORDED_USER).value(getRecordedUser() + "").build(),
                        Tag.builder().key(ORIGINATING_TEXT_CHANNEL).value(originatingTextChannel + "").build(),
                        Tag.builder().key(RECORDING_ID).value(recordingId).build())
                .build();
    }
}
