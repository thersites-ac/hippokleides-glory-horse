package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.embed.model.Subrule;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.function.Predicate;

public class DefaultFeatTransformer implements FeatTransformer {

    private static final String STAT_BLOCK_1 = "stat-block-1";
    private static final String STAT_BLOCK_2 = "stat-block-2";

    private Queue<Element> elements;

    @Override
    public Feat transformCoreFeat(List<Element> elements) {
        initializeElements(elements);
        String name = getValidName();
        String description = getValidDescription();
        List<FeatDetail> details = getDetails(e -> e.hasClass(STAT_BLOCK_1));
        String footer = getOptionalFooter();
        return Feat.builder()
                .name(name)
                .featDetails(details)
                .description(description)
                .footer(footer)
                .source("Core Rulebook")
                .build();
    }

    private void initializeElements(List<Element> elements) {
        this.elements = new LinkedList<>(elements);
    }

    @Override
    public Feat transformAdvancedClassFeat(List<Element> elements) {
        initializeElements(elements);
        String name = getValidName();
        String description = getValidDescription();
        List<FeatDetail> details = getDetails(e -> !e.children().isEmpty() && e.child(0).tagName().equals("strong"));
        String footer = getOptionalFooter();
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

    private String getValidDescription() {
        Element descriptor = elements.poll();
        validateDescriptor(descriptor);
        return descriptor.text();
    }

    private void validateDescriptor(Element descriptor) {
        if (descriptor == null || !descriptor.classNames().isEmpty() || ! descriptor.tagName().equals("p"))
            throw new ScrapedElementValidationException("description");
    }

    private String getOptionalFooter() {
        Element footerElement = elements.poll();
        if (footerElement != null && footerElement.hasClass(STAT_BLOCK_2))
            return footerElement.text();
        else
            return "Scraped with love by Hippokleides, Glory Horse";
    }

    private List<FeatDetail> getDetails(Predicate<Element> filter) {
        List<FeatDetail> result = new ArrayList<>();
        while (elements.peek() != null && filter.test(elements.peek()))
            result.add(consumeDetail());
        return result;
    }

    private FeatDetail consumeDetail() {
        Element detailMain = elements.remove();
        return FeatDetail.builder()
                .name(detailMain.child(0).text())
                .text(getDetailText(detailMain))
                .subrules(extractAllSubrules())
                .build();
    }

    private List<Subrule> extractAllSubrules() {
        List<Subrule> result = new ArrayList<>();
        while (topElementIsSubrule())
            result.add(consumeSubrule());
        return result;
    }

    private Subrule consumeSubrule() {
        Element subruleElement = elements.remove();
        return Subrule.builder()
                .name(subruleElement.child(0).text())
                .text(getDetailText(subruleElement))
                .build();
    }

    private boolean topElementIsSubrule() {
        Element element = elements.peek();
        return element != null
                && element.tagName().equals("p")
                && element.hasClass(STAT_BLOCK_2)
                && element.childrenSize() > 0
                && element.child(0).tagName().equals("i");
    }

    private String getDetailText(Element element) {
        dropFirstChild(element);
        String text = element.text();
        if (text.charAt(0) == ':')
            text = text.substring(1).strip();
        return text;
    }

    private void dropFirstChild(Element element) {
        element.childNodes().get(0).remove();
    }

    private String getValidName() {
        Element name = elements.poll();
        validateName(name);
        return name.text();
    }

    private void validateName(Element name) {
        if (name == null || !name.tagName().equals("h2"))
            throw new ScrapedElementValidationException("name");
    }

}
