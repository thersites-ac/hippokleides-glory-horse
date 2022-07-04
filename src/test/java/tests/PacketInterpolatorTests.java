package tests;

import cogbog.discord.audio.PacketInterpolator;
import cogbog.discord.audio.TimestampedPacket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cogbog.discord.constants.AudioConstants.PACKET_SIZE;
import static cogbog.discord.constants.AudioConstants.SILENCE_DATA;

@RunWith(JUnit4.class)
public class PacketInterpolatorTests {

    private List<TimestampedPacket> data;
    private byte[] input;
    private byte[] result;

    @Before
    public void setup() {
        data = new ArrayList<>();
    }

    @Test
    public void noInterpolationWithOnePacketOfSilence() {
        givenOnePacketOfSilence();
        whenInterpolate();
        thenGetOnePacketOfSilence();
    }

    @Test
    public void noInterpolationWithOnePacketOfSound() {
        givenOnePacketOfSound();
        whenInterpolate();
        thenGetOnePacketOfSound();
    }

    @Test
    public void interpolatesSilences() throws InterruptedException {
        givenSilenceSeparatedPackets();
        whenInterpolate();
        thenGetSevenPackets();
    }

    private void thenGetSevenPackets() {
        Assert.assertEquals(7 * PACKET_SIZE, result.length);
    }

    private void givenSilenceSeparatedPackets() throws InterruptedException {
        for (int i = 0; i < 5; i++)
            givenOnePacketOfSilence();
        Thread.sleep(21);
        givenOnePacketOfSound();
    }

    private void thenGetOnePacketOfSound() {
        Assert.assertArrayEquals(input, result);
    }

    private void givenOnePacketOfSound() {
        input = new byte[PACKET_SIZE];
        Arrays.fill(input, (byte) 1);
        data.add(new TimestampedPacket(input));
    }

    private void givenOnePacketOfSilence() {
        data.add(TimestampedPacket.silence());
    }

    private void whenInterpolate() {
        result = new PacketInterpolator(data).interpolate();
    }

    private void thenGetOnePacketOfSilence() {
        Assert.assertArrayEquals(SILENCE_DATA, result);
    }

}
