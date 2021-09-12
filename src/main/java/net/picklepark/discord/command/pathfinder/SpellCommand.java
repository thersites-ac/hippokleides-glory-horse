package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Catches;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.impl.LegacyPrdEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

@UserInput("spell (?<spell>.+)")
public class SpellCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(SpellCommand.class);

    private final LegacyPrdEmbedder embedder;

    @Inject
    public SpellCommand(LegacyPrdEmbedder embedder) {
        this.embedder = embedder;
    }

    @Override
    public void execute(DiscordActions actions) throws IOException, ResourceNotFoundException {
        MessageEmbed result = scrapeSpell(actions);
        actions.send(result);
    }

    @Catches(IllegalArgumentException.class)
    public void handleIllegalArgument(DiscordActions actions) {
        actions.send("I found the spell, but it's too big to send to Discord in one embed. Bug Aaron to fix this.");
    }

    @Catches(ResourceNotFoundException.class)
    public void notFound(DiscordActions actions) {
        actions.send("I couldn't find " + actions.getArgument("spell"));
    }

    private MessageEmbed scrapeSpell(DiscordActions actions) throws IOException, ResourceNotFoundException {
        String spell = actions.getArgument("spell");
        return embedder.embedSpell(spell);
    }
}
