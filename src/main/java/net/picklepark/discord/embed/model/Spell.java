package net.picklepark.discord.embed.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class Spell {
    private String name;
    private Map<String, String> qualifiers;
    private String description;
}
