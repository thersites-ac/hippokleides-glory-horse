package net.picklepark.discord.embed.transformer;

import net.picklepark.discord.embed.model.ScrapeResult;

public interface Transformer<T> {
    public T transform(ScrapeResult result);
}
