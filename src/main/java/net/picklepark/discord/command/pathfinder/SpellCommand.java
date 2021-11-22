package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.impl.LegacyPrdEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class SpellCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(SpellCommand.class);

    private final LegacyPrdEmbedder embedder;

    @Inject
    public SpellCommand(LegacyPrdEmbedder embedder) {
        this.embedder = embedder;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        MessageEmbed result = null;
        try {
            result = scrapeSpell(actions);
            actions.send(result);
        } catch (IOException e) {
            throw new DiscordCommandException(e);
        } catch (IllegalArgumentException e) {
            actions.send("I found the spell, but it's too big to send to Discord in one embed. Bug Aaron to fix this.");
        } catch (ResourceNotFoundException e) {
            actions.send("I couldn't find " + actions.getArgument("spell"));
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
    }

    @Override
    public String example() {
        return "spell <spell name>";
    }

    @Override
    public String helpMessage() {
        return "Look up a spell from legacy.aonprd.com.";
    }

    @Override
    public String userInput() {
        return "spell (?<spell>.+)";
    }

    private MessageEmbed scrapeSpell(DiscordActions actions) throws IOException, ResourceNotFoundException {
        String spell = actions.getArgument("spell");
        return embedder.embedSpell(spell);
    }
}
