package cogbog.discord.service;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

public interface AudioPlaybackService {
    void setChannelOne(AudioInputStream channelOne);
    void setChannelTwo(AudioInputStream channelTwo);
    boolean hasNext() throws IOException;
    byte[] nextTwentyMs() throws IOException;
}
