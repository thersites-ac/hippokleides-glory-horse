package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;

public interface EmbedRenderer {
    public MessageEmbed renderFeat(Feat feat);
}
