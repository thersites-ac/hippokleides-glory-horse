package net.picklepark.discord.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CanonicalKey {
    String guild;
    String key;

    @Override
    public String toString() {
        return guild + "/" + key;
    }
}
