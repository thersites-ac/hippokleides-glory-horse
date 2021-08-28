package net.picklepark.discord.embed.model;

import lombok.Builder;
import lombok.Data;
import org.jsoup.nodes.Element;

import java.util.List;

@Data
@Builder
public class ScrapeResult {
    private List<Element> elements;
    private String source;
    private String url;
}
