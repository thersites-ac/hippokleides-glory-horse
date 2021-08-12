package net.picklepark.discord.command.search.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.embed.Embedder;
import net.picklepark.discord.embed.renderer.EmbedRendererImpl;
import net.picklepark.discord.embed.scraper.ElementScraperImpl;

import java.io.IOException;

public class SearchCommand implements DiscordCommand {
    private final String elementId;
    private final Embedder embedder;
    private final GuildMessageReceivedEvent event;

    public SearchCommand(String elementId, GuildMessageReceivedEvent event) {
        embedder = new Embedder(new ElementScraperImpl(), new EmbedRendererImpl());
        this.elementId = elementId;
        this.event = event;
    }

    @Override
    public void execute() throws IOException {
        MessageEmbed embed = embedder.embedFeat(elementId);
        event.getChannel().sendMessageEmbeds(embed).queue();
    }
}
