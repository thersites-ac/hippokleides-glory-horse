package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;


public class FeatRenderer implements EmbedRenderer<Feat> {

    @Override
    public MessageEmbed render(Feat feat, String url, String author) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(feat.getName())
                .setDescription(feat.getDescription())
                .setAuthor(author, url);
        feat.getFeatDetails().forEach(detail -> builder.addField(detail.getName(), detail.getText(), false));
        return builder.build();
    }

}
