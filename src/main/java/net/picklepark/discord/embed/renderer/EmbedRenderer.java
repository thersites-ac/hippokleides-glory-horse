package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jsoup.nodes.Element;

import java.util.List;

public interface EmbedRenderer {
    public MessageEmbed renderCoreFeat(List<Element> elements);
}
