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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AudioMultichannelTests {

    private AudioPlaybackServiceImpl service;
    private AudioInputStream channelOne;
    private AudioInputStream channelTwo;
    private int numberOfPackets;
    private AudioPlaybackServiceImpl secondInstance;
    private List<byte[]> packets;
    private List<byte[]> otherPackets;

    @Before
    public void setup() throws IOException, UnsupportedAudioFileException {
        channelOne = initializeChannel("channel-1.wav");
        channelTwo = initializeChannel("channel-2.wav");
        service = new AudioPlaybackServiceImpl();
        packets = new ArrayList<>();
        otherPackets = new ArrayList<>();
    }

    @Test
    public void playsBackSingleChannel() throws IOException {
        givenChannelOne(channelOne);
        whenPlayAll();
        thenGetNumberOfPackets(230);
    }

    @Test
    public void playsBackLongerOfTwoChannels() throws IOException {
        givenChannelOne(channelOne);
        givenChannelOne(channelTwo);
        whenPlayAll();
        thenGetNumberOfPackets(735);
    }

    @Test
    public void combinedChannelDiffersFromIndividuals() throws IOException, UnsupportedAudioFileException {
        givenChannelOne(channelOne);
        givenChannelTwo(channelTwo);
        givenSeparateInstanceContaining(initializeChannel("channel-2.wav"));
        whenPlayAll();
        whenPlaySecondInstance();
        thenDataDoesNotMatch();
    }

    private void givenChannelTwo(AudioInputStream channelTwo) {
        service.setChannelTwo(channelTwo);
    }

    private void givenSeparateInstanceContaining(AudioInputStream channel) {
        secondInstance = new AudioPlaybackServiceImpl();
        secondInstance.setChannelOne(channel);
    }

    private void givenChannelOne(AudioInputStream channel) {
        service.setChannelOne(channel);
    }

    private void whenPlayAll() throws IOException {
        numberOfPackets = 0;
        while (service.hasNext()) {
            packets.add(service.nextTwentyMs());
            numberOfPackets++;
        }
    }

    private void whenPlaySecondInstance() throws IOException {
        while (secondInstance.hasNext()) {
            otherPackets.add(secondInstance.nextTwentyMs());
        }
    }

    private void thenGetNumberOfPackets(int n) {
        assertEquals(n, numberOfPackets);
    }

    private void thenDataDoesNotMatch() {
        for (int i = 0; i < packets.size(); i++)
            if (!Arrays.equals(packets.get(i), otherPackets.get(i)))
                return;

        fail();
    }

    private AudioInputStream initializeChannel(String filename) throws IOException, UnsupportedAudioFileException {
        InputStream raw = getClass().getResourceAsStream(filename);
        return AudioSystem.getAudioInputStream(raw);
    }

}