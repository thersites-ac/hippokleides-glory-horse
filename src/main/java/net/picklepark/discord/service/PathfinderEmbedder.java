package net.picklepark.discord.service;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.exception.ResourceNotFoundException;

import java.io.IOException;

public interface PathfinderEmbedder {
    public MessageEmbed embedCoreFeat(String id) throws IOException, ResourceNotFoundException;
    public MessageEmbed embedAdvancedPlayerFeat(String id) throws IOException, ResourceNotFoundException;
    public MessageEmbed embedAdvancedClassFeat(String id) throws IOException, ResourceNotFoundException;
    public MessageEmbed embedSpell(String spell) throws IOException, ResourceNotFoundException;
}
