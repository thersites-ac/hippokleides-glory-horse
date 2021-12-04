package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.model.Feat;
import net.picklepark.discord.model.ScrapeResult;
import net.picklepark.discord.service.ElementScraper;
import net.picklepark.discord.service.EmbedRenderer;
import net.picklepark.discord.service.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

public class FeatCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(FeatCommand.class);
    private static final String CORE_FEATS = "https://legacy.aonprd.com/coreRulebook/feats.html";
    private static final String ADVANCED_CLASS_FEATS = "https://legacy.aonprd.com/advancedClassGuide/feats.html";
    private static final String ADVANCED_PLAYER_FEATS = "https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html";

    private final ElementScraper scraper;
    private final EmbedRenderer<Feat> featRenderer;
    private final Transformer<Feat> featTransformer;

    @Inject
    public FeatCommand(ElementScraper scraper,
                       EmbedRenderer<Feat> featRenderer,
                       Transformer<Feat> featTransformer) {
        this.scraper = scraper;
        this.featRenderer = featRenderer;
        this.featTransformer = featTransformer;
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
            return embedWithSource(feat, ADVANCED_CLASS_FEATS, "Advanced Class Guide");
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private MessageEmbed advancePlayerFeatOrNull(String feat) throws IOException {
        try {
            return embedWithSource(feat, ADVANCED_PLAYER_FEATS, "Advanced Player's Guide");
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private MessageEmbed coreFeatOrNull(String feat) throws IOException {
        try {
            return embedWithSource(feat, CORE_FEATS, "Core Rulebook");
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private MessageEmbed embedWithSource(String id, String url, String source) throws IOException, ResourceNotFoundException {
        logger.info("Scraping {}", id);
        ScrapeResult result = scraper.scrapeFeatNodes(id, url);
        result.setSource(source);
        logger.info("Elements: {}", Arrays.toString(result.getElements().toArray()));
        Feat feat = featTransformer.transform(result);
        logger.info("Feat: {}", feat.toString());
        return featRenderer.render(feat);
    }

}