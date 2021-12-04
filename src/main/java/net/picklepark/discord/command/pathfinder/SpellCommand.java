package net.picklepark.discord.command.pathfinder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.ResourceNotFoundException;
import net.picklepark.discord.model.ScrapeResult;
import net.picklepark.discord.model.Spell;
import net.picklepark.discord.service.ElementScraper;
import net.picklepark.discord.service.EmbedRenderer;
import net.picklepark.discord.service.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

public class SpellCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(SpellCommand.class);
    private final ElementScraper scraper;
    private final EmbedRenderer<Spell> spellEmbedRenderer;
    private final Transformer<Spell> spellTransformer;

    @Inject
    public SpellCommand(ElementScraper scraper,
                        EmbedRenderer<Spell> spellEmbedRenderer,
                        Transformer<Spell> spellTransformer) {
        this.scraper = scraper;
        this.spellEmbedRenderer = spellEmbedRenderer;
        this.spellTransformer = spellTransformer;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        try {
            MessageEmbed result = scrapeSpell(actions);
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
        logger.info("Scraping {}", spell);
        ScrapeResult result = scraper.scrapeCoreSpell(spell);
        logger.info("Elements: {}", Arrays.toString(result.getElements().toArray()));
        Spell spellModel = spellTransformer.transform(result);
        logger.info("Spell: {}", spellModel.toString());
        return spellEmbedRenderer.render(spellModel);
    }
}
