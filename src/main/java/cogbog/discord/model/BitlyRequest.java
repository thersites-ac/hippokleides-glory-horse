package cogbog.discord.model;

import com.google.api.client.util.Key;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BitlyRequest {

    @Key("group_guid")
    private String groupGuid;

    @Key("long_url")
    private String url;

}
