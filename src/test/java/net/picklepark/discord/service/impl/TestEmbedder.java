package net.picklepark.discord.service.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.PathfinderEmbedder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class TestEmbedder implements PathfinderEmbedder {

    private Queue<EmbedGenerator> embeds;

    public TestEmbedder() {
        embeds = new LinkedList<>();
    }

    @Override
    public MessageEmbed embedCoreFeat(String id) throws IOException, ResourceNotFoundException {
        return embeds.element().core();
    }

    @Override
    public MessageEmbed embedAdvancedPlayerFeat(String id) throws IOException, ResourceNotFoundException {
        return embeds.element().advancedPlayer();
    }

    @Override
    public MessageEmbed embedAdvancedClassFeat(String id) throws IOException, ResourceNotFoundException {
        return embeds.element().advancedClass();
    }

    @Override
    public MessageEmbed embedSpell(String spell) throws IOException, ResourceNotFoundException {
        return embeds.element().spell();
    }

    public void addEmbed(MessageEmbed embed) {
        embeds.add(new IndiscriminateGenerator(embed));
    }

    public void addCore(MessageEmbed embed) {
        embeds.add(new CoreGenerator(embed));
    }

    public void addAdvancedPlayer(MessageEmbed embed) {
        embeds.add(new AdvancedPlayerGenerator(embed));
    }

    public void addAdvancedClass(MessageEmbed embed) {
        embeds.add(new AdvancedClassGenerator(embed));
    }

    private static class EmbedGenerator {
        EmbedGenerator() {}

        MessageEmbed core() throws ResourceNotFoundException {
            throw new ResourceNotFoundException();
        }

        MessageEmbed advancedPlayer() throws ResourceNotFoundException {
            throw new ResourceNotFoundException();
        }

        MessageEmbed advancedClass() throws ResourceNotFoundException {
            throw new ResourceNotFoundException();
        }

        MessageEmbed spell() throws ResourceNotFoundException {
            throw new ResourceNotFoundException();
        }
    }

    private class CoreGenerator extends EmbedGenerator {

        private final MessageEmbed embed;

        CoreGenerator(MessageEmbed embed) {
            this.embed = embed;
        }

        @Override
        MessageEmbed core() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }
    }

    private class AdvancedClassGenerator extends EmbedGenerator {
        private MessageEmbed embed;

        AdvancedClassGenerator(MessageEmbed embed) {
            this.embed = embed;
        }

        @Override
        MessageEmbed advancedClass() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }
    }

    private class AdvancedPlayerGenerator extends EmbedGenerator {
        private final MessageEmbed embed;

        AdvancedPlayerGenerator(MessageEmbed embed) {
            this.embed = embed;
        }

        @Override
        MessageEmbed advancedPlayer() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }
    }

    private class IndiscriminateGenerator extends EmbedGenerator {
        private final MessageEmbed embed;

        IndiscriminateGenerator(MessageEmbed embed) {
            this.embed = embed;
        }

        @Override
        MessageEmbed core() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }

        @Override
        MessageEmbed advancedPlayer() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }

        @Override
        MessageEmbed advancedClass() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }

        @Override
        MessageEmbed spell() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }
    }

    private class SpellGenerator extends EmbedGenerator {
        private final MessageEmbed embed;

        SpellGenerator(MessageEmbed embed) {
            this.embed = embed;
        }

        @Override
        MessageEmbed spell() throws ResourceNotFoundException {
            embeds.remove();
            return embed;
        }
    }

}
