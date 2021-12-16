package net.picklepark.discord.service.impl;


import net.picklepark.discord.exception.NotAvailableException;
import net.picklepark.discord.service.AudioPlaybackService;

import javax.sound.sampled.AudioInputStream;

import java.io.IOException;
import java.util.Arrays;

import static net.picklepark.discord.constants.AudioConstants.PACKET_SIZE;

public class AudioPlaybackServiceImpl implements AudioPlaybackService {

    private AudioInputStream channelOne;
    private AudioInputStream channelTwo;
    

    // todo: change this to ByteBuffer
    private final byte[] averageFrame = new byte[PACKET_SIZE];
    private final byte[] channelOneFrame = new byte[PACKET_SIZE];
    private final byte[] channelTwoFrame = new byte[PACKET_SIZE];
 
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
        int channelOneBytesRead = readChannel(channelOne, channelOneFrame);
        int channelTwoBytesRead = readChannel(channelTwo, channelTwoFrame);

        if (channelOneBytesRead == 0)
            channelOne = null;
        if (channelTwoBytesRead == 0)
            channelTwo = null;

        return channelOneBytesRead > 0 || channelTwoBytesRead > 0;
    }

    @Override
    public byte[] nextTwentyMs() {
        byte[] output;
        if (channelOne == null && channelTwo == null)
            throw new NotAvailableException();
        else if (channelOne == null)
            output = channelTwoFrame;
        else if (channelTwo == null)
            output = channelOneFrame;
        else {
            averageChannelFrames();
            output = averageFrame;
        }
        return Arrays.copyOf(output, PACKET_SIZE);
    }

    private void averageChannelFrames() {
        for (int i = 0; i < PACKET_SIZE; i++)
            averageFrame[i] = (byte) Math.max(channelOneFrame[i] >> 1 + channelTwoFrame[i] >> 1, 255);
    }

    private int readChannel(AudioInputStream channel, byte[] output) throws IOException {
        int bytesRead = 0;
        if (channel != null) {
            bytesRead = channel.read(output);
            if (bytesRead == 0)
                channel.close();
        }
        return  bytesRead;
    }

}