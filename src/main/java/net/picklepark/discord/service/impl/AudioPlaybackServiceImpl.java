package net.picklepark.discord.service.impl;


import net.picklepark.discord.exception.NotAvailableException;
import net.picklepark.discord.service.AudioPlaybackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;

import java.io.IOException;
import java.util.Arrays;

import static net.picklepark.discord.constants.AudioConstants.PACKET_SIZE;

public class AudioPlaybackServiceImpl implements AudioPlaybackService {

    private static final Logger logger = LoggerFactory.getLogger(AudioPlaybackServiceImpl.class);

    private AudioInputStream channelOne;
    private AudioInputStream channelTwo;
    
    // todo: change this to ByteBuffer
    private final byte[] averageFrame = new byte[PACKET_SIZE];
    private final byte[] channelOneFrame = new byte[PACKET_SIZE];
    private final byte[] channelTwoFrame = new byte[PACKET_SIZE];
    private int channelOneBytesRead = 0;
    private int channelTwoBytesRead = 0;

    @Override
    public void setChannelOne(AudioInputStream channelOne) {
        this.channelOne = channelOne;
    }

    @Override
    public void setChannelTwo(AudioInputStream channelTwo) {
        this.channelTwo = channelTwo;
    }

    @Override
    public boolean hasNext() throws IOException {
        channelOneBytesRead = readChannel(channelOne, channelOneFrame);
        channelTwoBytesRead = readChannel(channelTwo, channelTwoFrame);

        return channelOneBytesRead > 0 || channelTwoBytesRead > 0;
    }

    @Override
    public byte[] nextTwentyMs() {
        byte[] output;
        if (channelOneBytesRead <= 0 && channelTwoBytesRead <= 0) {
            throw new NotAvailableException();
        } else if (channelOneBytesRead <= 0) {
            output = channelTwoFrame;
        } else if (channelTwoBytesRead <= 0) {
            output = channelOneFrame;
        } else {
            averageChannelFrames();
            output = averageFrame;
        }
        return Arrays.copyOf(output, PACKET_SIZE);
    }

    private void averageChannelFrames() {
        for (int i = 0; i < PACKET_SIZE; i++) {
            byte summed = (byte) (channelOneFrame[i] + channelTwoFrame[i]);
            averageFrame[i] = summed;
        }
    }

    private int readChannel(AudioInputStream channel, byte[] output) throws IOException {
        if (channel == null)
            return 0;
        return channel.read(output);
    }

}