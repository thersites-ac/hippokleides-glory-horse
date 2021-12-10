package net.picklepark.discord.service;

import javax.sound.sampled.AudioInputStream;

public interface AudioPlaybackService {
    void addChannel(AudioInputStream datasource);
    boolean hasNext();
    byte[] nextTwentyMs();
}
