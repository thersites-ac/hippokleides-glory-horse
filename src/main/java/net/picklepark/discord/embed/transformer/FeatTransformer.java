package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Feat;
import org.jsoup.nodes.Element;

import java.util.List;

public interface FeatTransformer {
    public Feat transformCoreFeat(List<Element> elements);
}
