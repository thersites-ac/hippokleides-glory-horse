package cogbog.discord.service;

import cogbog.discord.model.ScrapeResult;

public interface Transformer<T> {
    public T transform(ScrapeResult result);
}
