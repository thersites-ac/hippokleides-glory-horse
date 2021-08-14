package net.picklepark.discord.command.pathfinder.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.embed.LegacyPrdEmbedder;
import net.picklepark.discord.embed.renderer.DefaultRenderer;
import net.picklepark.discord.embed.scraper.DefaultElementScraper;
import net.picklepark.discord.embed.transformer.DefaultFeatTransformer;

import java.io.IOException;

public class FeatCommand implements DiscordCommand {
    private final String elementId;
    private final LegacyPrdEmbedder legacyPrdEmbedder;
    private final GuildMessageReceivedEvent event;

    public FeatCommand(String elementId, GuildMessageReceivedEvent event) {
        legacyPrdEmbedder = new LegacyPrdEmbedder(new DefaultElementScraper(), new DefaultRenderer(), new DefaultFeatTransformer());
        this.elementId = elementId;
        this.event = event;
    }

    @Override
    public void execute() throws IOException {
        MessageEmbed embed = legacyPrdEmbedder.embedCoreFeat(elementId);
        event.getChannel().sendMessageEmbeds(embed).queue();
    }
}
