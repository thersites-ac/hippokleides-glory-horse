package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.Spell;
import net.picklepark.discord.exception.ScrapedElementValidationException;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSpellTransformer implements Transformer<Spell> {
    private List<Element> elements;

    @Override
    public Spell transform(List<Element> elements) {
        this.elements = elements;
        String name = consumeName();
        Map<String, String> qualifiers = consumeQualifiers();
        String description = consumeDescription();
        return Spell.builder()
                .name(name)
                .qualifiers(qualifiers)
                .description(description)
                .build();
    }

    private String consumeDescription() {
        String description = "";
        for (Element e: elements) {
            if (! e.tagName().equals("p"))
                throw new ScrapedElementValidationException("Description node has non-p tag: " + e.html());
            description += e.text() + "\n";
        }
        return description;
    }

    private Map<String, String> consumeQualifiers() {
        Map<String, String> qualifiers = new HashMap<>();
        while (nextElementIsQualifierNode()) {
            Map<String, String> subqualifiers = consumeNextQualifiers();
            qualifiers.putAll(subqualifiers);
        }
        return qualifiers;
    }

    private Map<String, String> consumeNextQualifiers() {
        Element chunk = elements.remove(0);
        Map<String, String> qualifiers = new HashMap<>();
        while (chunk.childrenSize() > 0) {
            String key = consumeBoldNode(chunk);
            String value = consumeTextNode(chunk);
            qualifiers.put(key, value);
        }
        return qualifiers;
    }

    private String consumeTextNode(Element chunk) {
        Node node = chunk.childNode(0);
        node.remove();
        if (! (node instanceof TextNode))
            throw new ScrapedElementValidationException("First child is not a text node: " + chunk.html());
        return ((TextNode) node).text();
    }

    private String consumeBoldNode(Element chunk) {
        Element e = chunk.child(0);
        if (!e.tagName().equals("b"))
            throw new ScrapedElementValidationException("First child is not a spell qualifier key: " + chunk.html());
        e.remove();
        return e.text();
    }

    private String consumeName() {
        return elements.remove(0).text();
    }

    private boolean nextElementIsQualifierNode() {
        return elements.get(0) != null && elements.get(0).hasClass("stat-block-1");
    }

}
