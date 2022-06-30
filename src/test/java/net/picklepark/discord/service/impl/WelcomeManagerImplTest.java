package net.picklepark.discord.service.impl;

import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.service.WelcomeManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class WelcomeManagerImplTest {

    private static final Long USER = 123L;
    private static final Long ANOTHER_USER = 234L;
    private static final String CHANNEL = "channel";
    private static final String ANOTHER_CHANNEL = "another channel";
    private static final String BUCKET = "bucket";
    private static final LocalClip CLIP = LocalClip.builder()
            .path("foo")
            .title("bar")
            .build();
    private static final LocalClip ANOTHER_CLIP = LocalClip.builder()
            .path("baz")
            .title("quux")
            .build();

    private WelcomeManager welcomeManager;
    private S3Client fetcher;

    @Before
    public void setup() {
        fetcher = mock(S3Client.class);
        when(fetcher.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());
        welcomeManager = new WelcomeManagerImpl(BUCKET, fetcher);
    }

    @Test
    public void fetchAfterPut() throws IOException {
        welcomeManager.set(USER, CHANNEL, CLIP);
        LocalClip result = welcomeManager.welcome(USER, CHANNEL);
        assertEquals(CLIP, result);
    }

    @Test
    public void nullWhenNoPreferenceSet() {
        LocalClip result = welcomeManager.welcome(USER, CHANNEL);
        assertNull(result);
    }

    @Test
    public void noDefaultWelcome() throws IOException {
        welcomeManager.set(USER, CHANNEL, CLIP);
        LocalClip result = welcomeManager.welcome(USER, "another channel");
        assertNull(result);
    }

    @Test
    public void canSetMultipleWelcomes() throws IOException {
        welcomeManager.set(USER, CHANNEL, CLIP);
        welcomeManager.set(USER, ANOTHER_CHANNEL, ANOTHER_CLIP);
        LocalClip result = welcomeManager.welcome(USER, CHANNEL);
        LocalClip anotherResult = welcomeManager.welcome(USER, ANOTHER_CHANNEL);
        assertEquals(CLIP, result);
        assertEquals(ANOTHER_CLIP, anotherResult);
    }

    @Test
    public void canWelcomeMultipleUsers() throws IOException {
        welcomeManager.set(USER, CHANNEL, CLIP);
        welcomeManager.set(ANOTHER_USER, CHANNEL, ANOTHER_CLIP);
        LocalClip result = welcomeManager.welcome(USER, CHANNEL);
        LocalClip anotherResult = welcomeManager.welcome(ANOTHER_USER, CHANNEL);
        assertEquals(CLIP, result);
        assertEquals(ANOTHER_CLIP, anotherResult);
    }
}