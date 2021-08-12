package net.picklepark.discord.embed.scraper.net;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class DocumentFetcherImplTests {

    @Test
    public void canCreate() {
        new DocumentFetcherImpl();
    }

    @Test
    public void canFetch() throws IOException {
        new DocumentFetcherImpl().fetch("http://www.google.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsBadUrlException() throws IOException {
        new DocumentFetcherImpl().fetch("foo");
    }
}
