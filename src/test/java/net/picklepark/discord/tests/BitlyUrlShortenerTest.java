package net.picklepark.discord.tests;

import com.google.api.client.http.*;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.gson.Gson;
import net.picklepark.discord.model.BitlyResponse;
import net.picklepark.discord.service.impl.BitlyUrlShortener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class BitlyUrlShortenerTest {

    private BitlyUrlShortener urlShortener;
    private String groupGuid;
    private HttpTransport transport;
    private String token;
    private LowLevelHttpResponse response;
    private String link;
    private String shortenedLink;

    @Before
    public void setup() {
        groupGuid = "foo";
        token = "bar";
        transport = new SpyMockHttpTransport();
        HttpRequestFactory factory = transport.createRequestFactory();
        urlShortener = new BitlyUrlShortener(groupGuid, token, "https://www.foo.com", factory);
    }

    @Test(expected = IOException.class)
    public void throwsServerErrorAsIoException() throws IOException {
        givenBitlyThrowsServerError();
        whenShorten();
    }

    @Test(expected = IOException.class)
    public void throwsUserErrorAsIoException() throws IOException {
        givenBitlyThrowsClientError();
        whenShorten();
    }

    @Test
    public void extractsSalientBodyField() throws IOException {
        givenBitlySucceeds();
        whenShorten();
        thenResultMatches();
    }

    private void givenBitlySucceeds() {
        link = "bar";
        BitlyResponse bitlyResponse = new BitlyResponse();
        bitlyResponse.setLink(link);
        String content = new Gson().toJson(bitlyResponse);
        response = new MockLowLevelHttpResponse().setContent(content);
    }

    private void givenBitlyThrowsClientError() {
        response = new MockLowLevelHttpResponse().setStatusCode(400);
    }


    private void givenBitlyThrowsServerError() {
        response = new MockLowLevelHttpResponse()
                .setStatusCode(500);
    }

    private void whenShorten() throws IOException {
        shortenedLink = urlShortener.shorten("foo.com");
    }

    private void thenResultMatches() {
        assertEquals(link, shortenedLink);
    }

    private class SpyMockHttpTransport extends HttpTransport {
        @Override
        protected LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
            return new MockLowLevelHttpRequest() {
                @Override
                public LowLevelHttpResponse execute() throws IOException {
                    return response;
                }
            };
        }
    }
}