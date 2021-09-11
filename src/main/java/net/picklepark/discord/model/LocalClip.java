package net.picklepark.discord.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalClip {
    private String path;
    private String title;
}