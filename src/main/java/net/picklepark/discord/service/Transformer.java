package net.picklepark.discord.service;

import net.picklepark.discord.model.ScrapeResult;

public interface Transformer<T> {
    public T transform(ScrapeResult result);
}
