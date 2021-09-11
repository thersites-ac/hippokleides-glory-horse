package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.service.impl.LegacyPrdEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SpellCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(SpellCommand.class);

    private final String spell;
    private final LegacyPrdEmbedder embedder;
    private MessageEmbed result;

    public SpellCommand(String spell, GuildMessageReceivedEvent event, LegacyPrdEmbedder embedder) {
        this.embedder = embedder;
        this.spell = spell;
        result = null;
    }

    @Override
    public void execute(DiscordActions actions) {
        scrapeSpell(actions);
        sendResult(actions);
    }

    private void sendResult(DiscordActions actions) {
        if (result != null)
            actions.send(result);
    }

    private void scrapeSpell(DiscordActions actions) {
        try {
            result = embedder.embedSpell(spell);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            actions.send("I found the spell, but it's too big to send to Discord in one embed. Bug Aaron to fix this.");
        } catch (Exception e) {
            logger.warn("Could not scrape {}", spell);
            e.printStackTrace();
            actions.send("Sorry, I couldn't find that.");
        }
    }
}
