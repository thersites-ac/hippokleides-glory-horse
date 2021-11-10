package net.picklepark.discord.service.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.PathfinderEmbedder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class TestEmbedder implements PathfinderEmbedder {

    private Queue<MessageEmbed> embeds;

    public TestEmbedder() {
        embeds = new LinkedList<>();
    }

    public void addEmbed(MessageEmbed embed) {
        embeds.add(embed);
    }

    @Override
    public MessageEmbed embedCoreFeat(String id) throws IOException, ResourceNotFoundException {
        return next();
    }

    @Override
    public MessageEmbed embedAdvancedPlayerFeat(String id) throws IOException, ResourceNotFoundException {
        return next();
    }

    @Override
    public MessageEmbed embedAdvancedClassFeat(String id) throws IOException, ResourceNotFoundException {
        return next();
    }

    @Override
    public MessageEmbed embedSpell(String spell) throws IOException, ResourceNotFoundException {
        return next();
    }

    private MessageEmbed next() {
        return embeds.remove();
    }
}
