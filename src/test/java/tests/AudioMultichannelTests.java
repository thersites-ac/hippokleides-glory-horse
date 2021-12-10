package tests;

import net.picklepark.discord.service.impl.AudioPlaybackServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AudioMultichannelTests {

    private AudioPlaybackServiceImpl service;
    private AudioInputStream channelOne;
    private AudioInputStream channelTwo;
    private int numberOfPackets;

    @Before
    public void setup() throws IOException, UnsupportedAudioFileException {
        channelOne = initializeChannel("channel-1.wav");
        channelTwo = initializeChannel("channel-2.wav");
        service = new AudioPlaybackServiceImpl();
    }

    @Test
    public void playsBackSingleChannel() throws IOException {
        givenChannelOfData();
        whenPlayAll();
        thenGetNumberOfPackets(4588);
    }

    private void givenChannelOfData() {
        service.addChannel(channelOne);
    }

    private void whenPlayAll() throws IOException {
        numberOfPackets = 0;
        while (service.hasNext()) {
            service.nextTwentyMs();
            numberOfPackets++;
        }
    }

    private void thenGetNumberOfPackets(int n) {
        assertEquals(n, numberOfPackets);
    }

    private AudioInputStream initializeChannel(String filename) throws IOException, UnsupportedAudioFileException {
        InputStream raw = getClass().getResourceAsStream(filename);
        return AudioSystem.getAudioInputStream(raw);
    }

}