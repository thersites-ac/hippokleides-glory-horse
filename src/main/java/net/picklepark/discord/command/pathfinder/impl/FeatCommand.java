package net.picklepark.discord.command.pathfinder.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.embed.Embedder;
import net.picklepark.discord.embed.renderer.EmbedRendererImpl;
import net.picklepark.discord.embed.scraper.ElementScraperImpl;

import java.io.IOException;

public class FeatCommand implements DiscordCommand {
    private final String elementId;
    private final Embedder embedder;
    private final GuildMessageReceivedEvent event;

    public FeatCommand(String elementId, GuildMessageReceivedEvent event) {
        embedder = new Embedder(new ElementScraperImpl(), new EmbedRendererImpl());
        this.elementId = elementId;
        this.event = event;
    }

    @Override
    public void execute() throws IOException {
        MessageEmbed embed = embedder.embedCoreFeat(elementId);
        event.getChannel().sendMessageEmbeds(embed).queue();
    }
}
