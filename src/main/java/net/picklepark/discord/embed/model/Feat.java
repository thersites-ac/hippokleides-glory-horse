package net.picklepark.discord.embed.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Feat {
    private String name;
    private List<FeatDetail> featDetails;
    private String footer;
}
