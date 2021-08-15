package net.picklepark.discord.embed.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FeatDetail {
    private String name;
    private String text;
    private List<Subrule> subrules;
}
