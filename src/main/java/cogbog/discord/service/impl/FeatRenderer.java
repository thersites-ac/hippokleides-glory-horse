package cogbog.discord.service.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import cogbog.discord.model.Feat;
import cogbog.discord.service.EmbedRenderer;

import javax.inject.Singleton;


@Singleton
public class FeatRenderer implements EmbedRenderer<Feat> {

    @Override
    public MessageEmbed render(Feat feat) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(feat.getName())
                .setDescription(feat.getDescription())
                .setAuthor(feat.getSource(), feat.getUrl());
        feat.getFeatDetails().forEach(detail -> builder.addField(detail.getName(), detail.getText(), false));
        return builder.build();
    }

}
