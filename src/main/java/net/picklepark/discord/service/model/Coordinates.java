package net.picklepark.discord.service.model;

import lombok.Builder;
import lombok.Getter;

import java.net.URL;

@Builder
@Getter
public class Coordinates {
    private final URL url;
    private final String key;
}
