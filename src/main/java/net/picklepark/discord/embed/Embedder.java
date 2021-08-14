package net.picklepark.discord.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Embedder {
    private static final String CORE_FEATS = "https://legacy.aonprd.com/coreRulebook/feats.html";
    private static final String ADVANCED_CLASS_FEATS = "https://legacy.aonprd.com/advancedClassGuide/feats.html";
    private static final String ADVANCED_PLAYER_FEATS = "https://legacy.aonprd.com/advancedPlayersGuide/advancedFeats.html";

    private ElementScraper scraper;
    private EmbedRenderer renderer;
    private static final Logger logger = LoggerFactory.getLogger(Embedder.class);

    public Embedder(ElementScraper scraper, EmbedRenderer renderer) {
        this.scraper = scraper;
        this.renderer = renderer;
    }

    public MessageEmbed embedCoreFeat(String id) throws IOException {
        List<Element> elements = scraper.scrapeFeatNodes(id, CORE_FEATS);
        logger.info("Elements: {}", Arrays.toString(elements.toArray()));
        Feat feat = transformCoreFeat(elements);
        logger.info("Feat: {}", feat.toString());
        return renderer.renderFeat(feat);
    }

    private Feat transformCoreFeat(List<Element> elements) {
        String name = getValidName(elements);
        String description = getValidDescription(elements);
        List<FeatDetail> details = getValidDetails(elements);
        String footer = getOptionalFooter(elements);
        return Feat.builder()
                .name(name)
                .featDetails(details)
                .description(description)
                .footer(footer)
                .source("Core Rulebook")
                .build();
    }

    private String getValidDescription(List<Element> elements) {
        Optional<Element> descriptor = elements.stream()
                .filter(e -> e.classNames().isEmpty() && e.tagName().equals("p"))
                .findFirst();
        if (descriptor.isPresent())
            return descriptor.get().text();
        else
            throw new ScrapedElementValidationException("description");
    }

    private String getOptionalFooter(List<Element> elements) {
        return elements.stream()
                .filter(e -> e.hasClass("stat-block-2"))
                .map(Element::text)
                .findFirst()
                .orElse("Scraped with love by Hippokleides, Glory Horse");
    }

    private List<FeatDetail> getValidDetails(List<Element> elements) {
        List<Element> detailParents = elements.stream()
                .filter(e -> e.hasClass("stat-block-1"))
                .collect(Collectors.toList());
        return detailParents.stream()
                .map(element -> FeatDetail.builder()
                        .name(element.child(0).text())
                        .text(getDetailText(element))
                        .build())
                .collect(Collectors.toList());
    }

    private String getDetailText(Element element) {
        dropFirstChild(element);
        return element.text();
    }

    private void dropFirstChild(Element element) {
        element.childNodes().get(0).remove();
    }

    private String getValidName(List<Element> elements) {
        List<Element> name = elements.stream()
                .filter(e -> e.tagName().equals("h2"))
                .collect(Collectors.toList());
        validateName(name);
        return name.get(0).text();
    }

    private void validateName(List<Element> name) {
        if (name.size() != 1) throw new ScrapedElementValidationException("name");
    }
}
