package net.picklepark.discord.embed.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Feat {
    private String name;
    private String description;
    private List<FeatDetail> featDetails;
}
