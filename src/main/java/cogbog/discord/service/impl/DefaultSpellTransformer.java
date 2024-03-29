package cogbog.discord.service.impl;

import cogbog.discord.exception.ScrapedElementValidationException;
import cogbog.discord.model.ScrapeResult;
import cogbog.discord.model.Spell;
import cogbog.discord.service.Transformer;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class DefaultSpellTransformer implements Transformer<Spell> {
    private List<Element> elements;

    @Override
    public Spell transform(ScrapeResult result) {
        this.elements = result.getElements();
        String name = consumeName();
        Map<String, String> qualifiers = consumeQualifiers();
        String description = consumeDescription();
        return Spell.builder()
                .name(name)
                .qualifiers(qualifiers)
                .description(description)
                .url(result.getUrl())
                .source(result.getSource())
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
            String value = consumeOtherNodes(chunk);
            qualifiers.put(key, value);
        }
        return qualifiers;
    }

    private String consumeOtherNodes(Element chunk) {
        String value = "";
        while (consumeNextChild(chunk))
            value += consumeSingleNonboldNode(chunk);
        return value;
    }

    private boolean consumeNextChild(Element chunk) {
        if (chunk.childNodeSize() == 0)
            return false;
        Node childNode = chunk.childNode(0);
        if (! (childNode instanceof Element))
            return true;
        else {
            Element e = (Element) childNode;
            return !e.tagName().equals("b");
        }
    }

    private String consumeSingleNonboldNode(Element chunk) {
        Node node = chunk.childNode(0);
        String result;
        if (node instanceof Element) {
            Element e = (Element) node;
            if (e.tagName().equals("b"))
                throw new ScrapedElementValidationException("Found bold element but expected spell qualifier description: " + chunk.html());
            else
                result = e.text();
        } else if (node instanceof TextNode)
            result = ((TextNode) node).text();
        else
            throw new ScrapedElementValidationException("Invalid node type: " + chunk.html());
        node.remove();
        return result;
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
