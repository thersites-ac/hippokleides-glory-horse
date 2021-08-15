package net.picklepark.discord.command.pathfinder.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.embed.LegacyPrdEmbedder;
import net.picklepark.discord.embed.renderer.DefaultRenderer;
import net.picklepark.discord.embed.scraper.DefaultElementScraper;
import net.picklepark.discord.embed.transformer.DefaultFeatTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(FeatCommand.class);

    private final String elementId;
    private final LegacyPrdEmbedder legacyPrdEmbedder;
    private final GuildMessageReceivedEvent event;

    private MessageEmbed foundFeat;

    public FeatCommand(String elementId, GuildMessageReceivedEvent event) {
        legacyPrdEmbedder = new LegacyPrdEmbedder(new DefaultElementScraper(), new DefaultRenderer(), new DefaultFeatTransformer());
        this.elementId = elementId;
        this.event = event;
        foundFeat = null;
    }

    @Override
    public void execute() {
        tryCoreFeat();
        if (foundFeat == null)
            tryAdvancedPlayerFeat();
        if (foundFeat == null)
            tryAdvancedClassFeat();
        if (foundFeat == null)
            event.getChannel().sendMessage("Sorry, I couldn't find that feat").queue();
        else
            event.getChannel().sendMessageEmbeds(foundFeat).queue();
    }

    private void tryAdvancedClassFeat() {
        try {
            foundFeat = legacyPrdEmbedder.embedAdvancedClassFeat(elementId);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private void tryAdvancedPlayerFeat() {
        try {
            foundFeat = legacyPrdEmbedder.embedAdvancedPlayerFeat(elementId);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private void tryCoreFeat() {
        try {
            foundFeat = legacyPrdEmbedder.embedCoreFeat(elementId);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
