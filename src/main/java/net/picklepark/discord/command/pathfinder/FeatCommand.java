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

    @Inject
    public FeatCommand(LegacyPrdEmbedder embedder) {
        this.legacyPrdEmbedder = embedder;
    }

    @Override
    public void execute(DiscordActions actions) {
        String feat = actions.getArgument("feat");
        try {
            MessageEmbed foundFeat = legacyPrdEmbedder.embedCoreFeat(feat);
            if (foundFeat == null)
                foundFeat = legacyPrdEmbedder.embedAdvancedPlayerFeat(feat);
            if (foundFeat == null)
                foundFeat = legacyPrdEmbedder.embedAdvancedClassFeat(feat);
            if (foundFeat == null)
                actions.send("Sorry, I couldn't find that feat");
            else
                actions.send(foundFeat);
        } catch (Exception e) {
            logger.warn("While looking up feat + " + feat, e);
        }
    }

}