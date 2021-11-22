package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.service.PathfinderEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class FeatCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(FeatCommand.class);

    private final PathfinderEmbedder embedder;

    @Inject
    public FeatCommand(PathfinderEmbedder embedder) {
        this.embedder = embedder;
    }

    @Override
    public void execute(DiscordActions actions) {
        String feat = actions.getArgument("feat");
        try {
            MessageEmbed foundFeat = coreFeatOrNull(feat);
            if (foundFeat == null)
                foundFeat = advancePlayerFeatOrNull(feat);
            if (foundFeat == null)
                foundFeat = advancedClassFeatOrNull(feat);
            if (foundFeat == null)
                actions.send("Sorry, I couldn't find that feat");
            else
                actions.send(foundFeat);
        } catch (IOException e) {
            logger.error("While looking up feat " + feat, e);
            actions.send("Having trouble with my interwebs");
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
    }

    @Override
    public String example() {
        return "feat <feat name>";
    }

    @Override
    public String helpMessage() {
        return "Look up a feat from legacy.aonprd.com.";
    }

    @Override
    public String userInput() {
        return "feat (?<feat>.+)";
    }

    private MessageEmbed advancedClassFeatOrNull(String feat) throws IOException {
        try {
            return embedder.embedAdvancedClassFeat(feat);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private MessageEmbed advancePlayerFeatOrNull(String feat) throws IOException {
        try {
            return embedder.embedAdvancedPlayerFeat(feat);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private MessageEmbed coreFeatOrNull(String feat) throws IOException {
        try {
            return embedder.embedCoreFeat(feat);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
}