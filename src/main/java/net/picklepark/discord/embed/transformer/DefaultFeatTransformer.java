package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultFeatTransformer implements FeatTransformer {

    @Override
    public Feat transformCoreFeat(List<Element> elements) {
        String name = getValidName(elements);
        String description = getValidDescription(elements);
        List<FeatDetail> details = getDetails(elements, e -> e.hasClass("stat-block-1"));
        String footer = getOptionalFooter(elements);
        return Feat.builder()
                .name(name)
                .featDetails(details)
                .description(description)
                .footer(footer)
                .source("Core Rulebook")
                .build();
    }

    @Override
    public Feat transformAdvancedClassFeat(List<Element> elements) {
        String name = getValidName(elements);
        String description = getValidDescription(elements);
        List<FeatDetail> details = getDetails(elements,
                e -> !e.children().isEmpty()
                        && e.child(0).tagName().equals("strong"));
        String footer = getOptionalFooter(elements);
        return Feat.builder()
                .name(name)
                .featDetails(details)
                .description(description)
                .footer(footer)
                .source("Advanced Class Guide")
                .build();
    }

    @Override
    public Feat transformAdvancedPlayerFeat(List<Element> elements) {
        Feat result = transformCoreFeat(elements);
        result.setSource("Advanced Player's Guide");
        return result;
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

    private List<FeatDetail> getDetails(List<Element> elements, Predicate<Element> filter) {
        List<Element> detailParents = elements.stream()
                .filter(filter)
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
