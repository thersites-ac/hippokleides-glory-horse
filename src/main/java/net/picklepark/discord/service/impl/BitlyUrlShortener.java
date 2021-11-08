package net.picklepark.discord.service.impl;

import com.google.api.client.http.*;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import net.picklepark.discord.model.BitlyRequest;
import net.picklepark.discord.model.BitlyResponse;
import net.picklepark.discord.service.UrlShortener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStreamReader;

public class BitlyUrlShortener implements UrlShortener {

    private final HttpRequestFactory requestFactory;
    private final GenericUrl endpoint;
    private final HttpHeaders headers;
    private final JsonFactory gsonFactory;
    private final String groupGuid;
    private static final Logger logger = LoggerFactory.getLogger(BitlyUrlShortener.class);

    @Inject
    public BitlyUrlShortener(@Named("shortener.group.guid") String groupGuid,
                             @Named("shortener.auth.token") String token,
                             @Named("shortener.endpoint") String endpoint,
                             HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
        this.groupGuid = groupGuid;
        this.headers = new HttpHeaders()
                .setAuthorization("Bearer " + token)
                .setAccept("application/json");
        this.endpoint = new GenericUrl(endpoint);
        gsonFactory = new GsonFactory();
    }

    @Override
    public String shorten(String url) throws IOException {
        HttpRequest request = requestFactory
                .buildPostRequest(endpoint, contentFor(url))
                .setHeaders(headers);
        Gson gson = new Gson();
        HttpResponse response = request.execute();
        BitlyResponse bitlyResponse = gson.fromJson(new InputStreamReader(response.getContent()), BitlyResponse.class);
        return bitlyResponse.getLink();
    }

    private HttpContent contentFor(String url) {
        BitlyRequest bitlyRequest = BitlyRequest.builder()
                .groupGuid(groupGuid)
                .url(url)
                .build();
        return new JsonHttpContent(gsonFactory, bitlyRequest);
    }
}
