package net.picklepark.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import net.picklepark.discord.adaptor.LavaPlayerInputStreamAdaptor;
import net.picklepark.discord.service.AudioPlaybackService;
import net.picklepark.discord.service.impl.AudioPlaybackServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;

import static net.dv8tion.jda.api.audio.AudioReceiveHandler.OUTPUT_FORMAT;
import static net.picklepark.discord.constants.AudioConstants.BYTES_PER_MS;

public class GuildPlayer {

  private static final Logger logger = LoggerFactory.getLogger(GuildPlayer.class);

  public final AudioPlayer player;
  public final AudioPlaybackService audioPlaybackService;
  private final LavaPlayerInputStreamAdaptor adaptor;

  public GuildPlayer(AudioPlayerManager manager) {
    player = manager.createPlayer();
    audioPlaybackService = new AudioPlaybackServiceImpl();
    adaptor = new LavaPlayerInputStreamAdaptor(player);
  }

  public void queue(AudioTrack track) {
    logger.info("Queueing to channel one");
    try {
      player.playTrack(track);
      player.setVolume(30);
      while (!track.getState().equals(AudioTrackState.PLAYING));
      audioPlaybackService.setChannelOne(new AudioInputStream(
              adaptor,
              OUTPUT_FORMAT,
              // this parameter should be named `lengthInFrames` or `totalFrames`; it's incredibly confusing as is
              // compute the total number of frames in the stream by dividing its total size by the size of the output format frame
              // the ratio is actually 48 bytes/ms, matching the sample rate
              track.getDuration() * BYTES_PER_MS / OUTPUT_FORMAT.getFrameSize()
      ));
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }

}
