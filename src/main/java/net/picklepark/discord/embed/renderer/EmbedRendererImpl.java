package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;


public class EmbedRendererImpl implements EmbedRenderer {
    @Override
    public MessageEmbed renderFeat(Feat feat) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(feat.getName())
                .setDescription(feat.getDescription())
                .setFooter(feat.getFooter());
        feat.getFeatDetails()
                .forEach(detail -> builder.addField(detail.getName(), detail.getText(), false));
        return builder.build();
    }
}
