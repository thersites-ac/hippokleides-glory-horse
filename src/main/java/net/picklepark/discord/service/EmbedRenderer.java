package net.picklepark.discord.service;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface EmbedRenderer<T> {
    public MessageEmbed render(T input);
}
