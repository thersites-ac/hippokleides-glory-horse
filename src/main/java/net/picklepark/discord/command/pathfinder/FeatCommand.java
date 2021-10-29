package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.service.impl.LegacyPrdEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@UserInput("feat (?<feat>.+)")
@Help(name = "feat <feat name>", message = "Look up a feat from legacy.aonprd.com.")
public class FeatCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(FeatCommand.class);

    private final LegacyPrdEmbedder legacyPrdEmbedder;

    private MessageEmbed foundFeat;

    @Inject
    public FeatCommand(LegacyPrdEmbedder embedder) {
        this.legacyPrdEmbedder = embedder;
        foundFeat = null;
    }

    @Override
    public void execute(DiscordActions actions) {
        String feat = actions.getArgument("feat");
        tryCoreFeat(feat);
        if (foundFeat == null)
            tryAdvancedPlayerFeat(feat);
        if (foundFeat == null)
            tryAdvancedClassFeat(feat);
        if (foundFeat == null)
            actions.send("Sorry, I couldn't find that feat");
        else
            actions.send(foundFeat);
    }

    private void tryAdvancedClassFeat(String feat) {
        try {
            foundFeat = legacyPrdEmbedder.embedAdvancedClassFeat(feat);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private void tryAdvancedPlayerFeat(String feat) {
        try {
            foundFeat = legacyPrdEmbedder.embedAdvancedPlayerFeat(feat);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private void tryCoreFeat(String feat) {
        try {
            foundFeat = legacyPrdEmbedder.embedCoreFeat(feat);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
