package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.service.impl.LegacyPrdEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SpellCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(SpellCommand.class);

    private final String spell;
    private final LegacyPrdEmbedder embedder;
    private final GuildMessageReceivedEvent event;
    private MessageEmbed result;

    public SpellCommand(String spell, GuildMessageReceivedEvent event, LegacyPrdEmbedder embedder) {
        this.embedder = embedder;
        this.spell = spell;
        this.event = event;
        result = null;
    }

    @Override
    public void execute() throws IOException {
        scrapeSpell();
        sendResult();
    }

    private void sendResult() {
        if (result != null)
            event.getChannel().sendMessageEmbeds(result).queue();
    }

    private void scrapeSpell() {
        try {
            result = embedder.embedSpell(spell);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            event.getChannel().sendMessage("I found the spell, but it's too big to send to Discord in one embed. Bug Aaron to fix this.").queue();
        } catch (Exception e) {
            logger.warn("Could not scrape {}", spell);
            e.printStackTrace();
            event.getChannel().sendMessage("Sorry, I couldn't find that.").queue();
        }
    }
}
