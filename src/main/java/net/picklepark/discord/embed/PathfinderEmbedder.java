package net.picklepark.discord.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;

public interface PathfinderEmbedder {
    public MessageEmbed embedCoreFeat(String id) throws IOException;
    public MessageEmbed embedAdvancedPlayerFeat(String id) throws IOException;
    public MessageEmbed embedAdvancedClassFeat(String id) throws IOException;
}
