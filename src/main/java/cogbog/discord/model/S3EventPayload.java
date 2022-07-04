package cogbog.discord.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class S3EventPayload {
    private BucketReference bucket;
    private ObjectReference object;
}
