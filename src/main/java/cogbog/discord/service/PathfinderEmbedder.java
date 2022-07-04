package cogbog.discord.service;

import cogbog.discord.exception.ResourceNotFoundException;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;

public interface PathfinderEmbedder {
    public MessageEmbed embedCoreFeat(String id) throws IOException, ResourceNotFoundException;
    public MessageEmbed embedAdvancedPlayerFeat(String id) throws IOException, ResourceNotFoundException;
    public MessageEmbed embedAdvancedClassFeat(String id) throws IOException, ResourceNotFoundException;
    public MessageEmbed embedSpell(String spell) throws IOException, ResourceNotFoundException;
}
