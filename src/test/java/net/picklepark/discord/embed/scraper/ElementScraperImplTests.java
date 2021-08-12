package net.picklepark.discord.embed.scraper;

import net.picklepark.discord.embed.scraper.net.DocumentFetcher;
import net.picklepark.discord.exception.NotFoundException;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class ElementScraperImplTests {

    private Document result;

    private DocumentFetcher failure = new DocumentFetcher() {
        @Override
        public Document fetch(String url) throws IOException {
            throw new IOException();
        }
    };

    private DocumentFetcher mockFetcher = new DocumentFetcher() {
        @Override
        public Document fetch(String url) throws IOException {
            return result;
        }
    };

    private ElementScraperImpl failureScraper = new ElementScraperImpl(failure);
    private ElementScraperImpl mockScraper = new ElementScraperImpl(mockFetcher);

    @Test(expected = IOException.class)
    public void propagatesBadUrlException() throws IOException {
        failureScraper.scrapeCoreFeat("foo");
    }

    @Test(expected = NotFoundException.class)
    public void throwsExceptionWhenElementNotFound() throws IOException {
        result = new Document("foo");
        mockScraper.scrapeCoreFeat("foo");
    }

}
