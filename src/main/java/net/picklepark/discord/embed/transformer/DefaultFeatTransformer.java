package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Feat;
import net.picklepark.discord.embed.model.FeatDetail;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class DefaultFeatTransformer implements Transformer<Feat> {

    private static final String BENEFIT = "Benefit";
    private static final String NORMAL = "Normal";
    private static final String PREREQUISITE = "Prerequisite";
    private static final String SPECIAL = "Special";

    private Queue<Element> elements;
    private boolean consumeSubparagraphs = true;

    @Override
    public Feat transform(List<Element> elements) {
        initializeElements(elements);
        String name = nextName();
        String description = nextDescription();
        List<FeatDetail> details = nextDetails();
        checkEverythingUsedUp();
        return Feat.builder()
                .name(name)
                .featDetails(details)
                .description(description)
                .build();
    }

    private void checkEverythingUsedUp() {
        if (!elements.isEmpty()) {
            String message = "Scraped unconsumed data: " + new Elements(elements).html();
            throw new ScrapedElementValidationException(message);
        }
    }

    private void initializeElements(List<Element> elements) {
        this.elements = new LinkedList<>(elements);
    }

    private String nextDescription() {
        if (nextElementIsDetail())
            throw new ScrapedElementValidationException("Missing description");
        Element descriptor = elements.remove();
        validateDescriptor(descriptor);
        return descriptor.text();
    }

    private void validateDescriptor(Element descriptor) {
        if (descriptor == null || ! descriptor.tagName().equals("p"))
            throw new ScrapedElementValidationException("Invalid description");
    }

    private List<FeatDetail> nextDetails() {
        List<FeatDetail> result = new ArrayList<>();
        while (nextElementIsDetail())
            result.add(nextDetail());
        return result;
    }

    private boolean nextElementIsDetail() {
        Element next = elements.peek();
        if (next == null || next.children().isEmpty())
            return false;
        else {
            String text = next.child(0).text();
            return text.contains(BENEFIT)
                    || text.contains(NORMAL)
                    || text.contains(PREREQUISITE)
                    || text.contains(SPECIAL);
        }
    }

    private FeatDetail nextDetail() {
        Element detailMain = elements.remove();
        String mainText = detailMain.child(0).text();
        consumeSubparagraphs = mainText.contains(BENEFIT);
        return FeatDetail.builder()
                .name(detailMain.child(0).text())
                .text(getDetailText(detailMain))
                .build();
    }

    private String getDetailText(Element element) {
        dropFirstChild(element);
        String text = element.text();
        if (text.charAt(0) == ':')
            text = text.substring(1).strip();
        List<Element> subparagraphs = nextSubparagraphs();
        return formatDetail(text, subparagraphs);
    }

    private String formatDetail(String text, List<Element> subparagraphs) {
        for (Element e: subparagraphs)
            text += "\n" + e.text();
        return text;
    }

    private List<Element> nextSubparagraphs() {
        List<Element> subparagraphs = new ArrayList<>();
        while (consumeSubparagraphs && !elements.isEmpty() && !nextElementIsDetail())
            subparagraphs.add(elements.remove());
        return subparagraphs;
    }

    private void dropFirstChild(Element element) {
        element.childNodes().get(0).remove();
    }

    private String nextName() {
        Element name = elements.poll();
        validateName(name);
        return name.text();
    }

    private void validateName(Element name) {
        if (name == null || !name.tagName().equals("h2"))
            throw new ScrapedElementValidationException("Invalid name");
    }

}
