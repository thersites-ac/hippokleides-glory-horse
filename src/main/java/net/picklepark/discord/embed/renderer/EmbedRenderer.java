package net.picklepark.discord.embed.renderer;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface EmbedRenderer<T> {
    public MessageEmbed render(T input, String url, String author);
    public default MessageEmbed render(T input) {
        return render(input, "", "");
    }
}
