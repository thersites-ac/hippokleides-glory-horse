package tests;

import cogbog.discord.audio.DiscontinuousAudioArray;
import cogbog.discord.exception.InvalidAudioPacketException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static cogbog.discord.constants.AudioConstants.PACKET_SIZE;

@RunWith(JUnit4.class)
public class DiscontinuousAudioArrayTests {

    private DiscontinuousAudioArray discontinuousAudioArray;
    private byte[] data;

    @Before
    public void setup() {
        discontinuousAudioArray = new DiscontinuousAudioArray();
        data = new byte[PACKET_SIZE];
    }

    @Test
    public void storesAudio() throws InvalidAudioPacketException {
        whenProvideAudio();
        thenCanRetrieveIt();
    }

    @Test
    public void canSetMaximumRecordingLength() throws InterruptedException, InvalidAudioPacketException {
        givenShortMaxRecordingLength();
        whenInterpolateLongAudio();
        thenResultDropsOldPackets();
    }

    private void givenShortMaxRecordingLength() {
        discontinuousAudioArray = new DiscontinuousAudioArray(40);
    }

    private void whenInterpolateLongAudio() throws InterruptedException, InvalidAudioPacketException {
        whenProvideAudio();
        Thread.sleep(41);
        whenProvideAudio();
    }

    private void whenProvideAudio() throws InvalidAudioPacketException {
        discontinuousAudioArray.store(data);
    }

    private void thenResultDropsOldPackets() {
        Assert.assertEquals(PACKET_SIZE, discontinuousAudioArray.retrieve().length);
    }

    private void thenCanRetrieveIt() {
        Assert.assertArrayEquals(data, discontinuousAudioArray.retrieve());
    }

}
