package net.picklepark.discord.model;

import lombok.Builder;
import lombok.Value;
import net.picklepark.discord.exception.MalformedKeyException;

@Builder
@Value
public class CanonicalKey {
    String guild;
    String key;

    @Override
    public String toString() {
        return guild + "/" + key;
    }

    public static CanonicalKey fromString(String s) throws MalformedKeyException {
        var results = s.split("/");
        if (results.length != 2)
            throw new MalformedKeyException(s);
        else
            return CanonicalKey.builder()
                    .guild(results[0])
                    .key(results[1])
                    .build();
    }
}
