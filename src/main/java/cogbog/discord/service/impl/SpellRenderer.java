package cogbog.discord.service.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import cogbog.discord.model.Spell;
import cogbog.discord.service.EmbedRenderer;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class SpellRenderer implements EmbedRenderer<Spell> {

    @Override
    public MessageEmbed render(Spell input) {
        String url = input.getUrl();
        String source = input.getSource();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(input.getName());
        for (Map.Entry<String, String> pair: input.getQualifiers().entrySet()) {
            builder.addField(pair.getKey(), pair.getValue(), true);
        }
        builder.addField("Spell Description", input.getDescription(), false);
        builder.setAuthor(source, url);
        return builder.build();
    }

}
