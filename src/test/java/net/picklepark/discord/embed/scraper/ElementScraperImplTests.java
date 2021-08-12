package net.picklepark.discord.embed.scraper;

import net.picklepark.discord.embed.scraper.net.DocumentFetcher;
import net.picklepark.discord.exception.NotFoundException;
import net.picklepark.discord.exception.NullDocumentException;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class ElementScraperImplTests {

    private Document result;
    private Exception exception;

    @Test(expected = IOException.class)
    public void propagatesBadUrlException() throws IOException {
        ElementScraperImpl failure = new ElementScraperImpl(new ThrowFetcher());
        exception = new IOException();
        failure.scrapeCoreFeat("foo");
    }

    @Test(expected = NotFoundException.class)
    public void throwsExceptionWhenElementNotFound() throws IOException {
        ElementScraperImpl mock = new ElementScraperImpl(new MockFetcher());
        result = new Document("foo");
        mock.scrapeCoreFeat("foo");
    }

    @Test(expected = NullDocumentException.class)
    public void throwsExceptionWhenFetcherReturnsNull() throws IOException {
        ElementScraperImpl mock = new ElementScraperImpl(new MockFetcher());
        result = null;
        mock.scrapeCoreFeat("foo");
    }

    private class ThrowFetcher implements DocumentFetcher {
        @Override
        public Document fetch(String url) throws IOException {
            try {
                throw exception;
            } catch (IOException | RuntimeException e) {
                throw e;
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    private class MockFetcher implements DocumentFetcher {
        @Override
        public Document fetch(String url) throws IOException {
            return result;
        }
    }

}
