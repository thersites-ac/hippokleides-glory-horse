package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.embed.model.Subrule;

import java.util.List;


public class DefaultRenderer implements EmbedRenderer {

    @Override
    public MessageEmbed renderFeat(Feat feat) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(feat.getName())
                .setDescription(feat.getDescription())
                .setAuthor(feat.getSource())
                .setFooter(feat.getFooter());
        feat.getFeatDetails()
                .forEach(detail -> {
                    builder.addField(detail.getName(), detail.getText(), false);
                    addSubruleFields(builder, detail);
                });
        return builder.build();
    }

    private void addSubruleFields(EmbedBuilder builder, FeatDetail detail) {
        List<Subrule> subrules = detail.getSubrules();
        if (subrules != null)
            subrules.forEach(subrule -> builder.addField(subrule.getName(), subrule.getText(), true));

    }
}
