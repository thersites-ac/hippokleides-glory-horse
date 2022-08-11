package tools;

import cogbog.discord.service.UrlShortener;

import java.io.IOException;

public class PassthroughUrlShortener implements UrlShortener {
    @Override
    public String shorten(String url) throws IOException {
        return url;
    }
}
