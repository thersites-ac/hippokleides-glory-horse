package net.picklepark.discord.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.embed.renderer.EmbedRenderer;
import net.picklepark.discord.embed.scraper.ElementScraper;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Embedder {

    private ElementScraper scraper;
    private EmbedRenderer renderer;

    public Embedder(ElementScraper scraper, EmbedRenderer renderer) {
        this.scraper = scraper;
        this.renderer = renderer;
    }

    public MessageEmbed embedFeat(String id) throws IOException {
        List<Element> elements = scraper.scrapeCoreFeat(id);
        Feat feat = transform(elements);
        return renderer.renderFeat(feat);
    }

    private Feat transform(List<Element> elements) {
        String name = getValidName(elements);
        List<FeatDetail> details = getValidDetails(elements);
        String footer = getValidFooter(elements);
        return Feat.builder()
                .name(name)
                .featDetails(details)
                .footer(footer)
                .build();
    }

    private String getValidFooter(List<Element> elements) {
        List<Element> footer = elements.stream()
                .filter(e -> e.hasClass("stat-block-2"))
                .collect(Collectors.toList());
        validateFooter(footer);
        return footer.get(0).text();
    }

    private List<FeatDetail> getValidDetails(List<Element> elements) {
        List<Element> qualifiers = elements.stream()
                .filter(e -> e.hasClass("stat-block-1"))
                .collect(Collectors.toList());
        validateQualifiers(qualifiers);
        return qualifiers.stream()
                .map(element -> FeatDetail.builder()
                        .name(element.child(0).text())
                        .text(element.textNodes().get(0).getWholeText())
                        .build())
                .collect(Collectors.toList());
    }

    private String getValidName(List<Element> elements) {
        List<Element> name = elements.stream()
                .filter(e -> e.tagName().equals("h2"))
                .collect(Collectors.toList());
        validateName(name);
        return name.get(0).text();
    }

    private void validateFooter(List<Element> footer) {
        if (footer.size() != 1) throw new ScrapedElementValidationException("footer");
    }

    private void validateQualifiers(List<Element> qualifiers) {
        qualifiers.forEach(qualifier -> {
            if (qualifier.childNodeSize() != 2) throw new ScrapedElementValidationException("qualifier");
        });
    }

    private void validateName(List<Element> name) {
        if (name.size() != 1) throw new ScrapedElementValidationException("name");
    }
}
