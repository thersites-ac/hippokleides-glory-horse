package net.picklepark.discord.embed.command.audio.util;

import net.picklepark.discord.command.audio.util.DiscontinuousAudioArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class DiscontinuousAudioArrayTests {

    private DiscontinuousAudioArray discontinuousAudioArray;
    private byte[] data;

    @Before
    public void setup() {
        discontinuousAudioArray = new DiscontinuousAudioArray();
        data = new byte[DiscontinuousAudioArray.PACKET_SIZE];
    }

    @Test
    public void storesAudio() {
        whenProvideAudio();
        thenCanRetrieveIt();
    }

    @Test
    public void dropsDataOverOneMinute() {
        Assert.fail();
    }

    private void givenDataIsSilence() {
        Arrays.fill(data, (byte) 0);
    }

    private void whenProvideAudio() {
        discontinuousAudioArray.store(data);
    }

    private void thenCanRetrieveIt() {
        Assert.assertArrayEquals(data, discontinuousAudioArray.retrieve());
    }

}
