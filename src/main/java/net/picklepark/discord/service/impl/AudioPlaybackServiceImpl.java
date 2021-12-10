package net.picklepark.discord.service.impl;


import net.picklepark.discord.service.AudioPlaybackService;

import javax.sound.sampled.AudioInputStream;

import java.io.IOException;
import java.util.Arrays;

import static net.picklepark.discord.constants.AudioConstants.BYTES_PER_MS;

public class AudioPlaybackServiceImpl implements AudioPlaybackService {

    private AudioInputStream channel;
    // todo: change this to ByteBuffer
    private byte[] frame = new byte[BYTES_PER_MS];

    @Override
    public void addChannel(AudioInputStream datasource) {
        channel = datasource;
    }

    @Override
    public boolean hasNext() {
        try {
            return channel.read(frame) > 0;
        } catch (IOException e) {
            // fixme: should this propagate the exception?
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public byte[] nextTwentyMs() {
        // todo: immutability
        return frame;
    }
}
