package net.picklepark.discord.embed.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeatDetail {
    private String name;
    private String text;
}
