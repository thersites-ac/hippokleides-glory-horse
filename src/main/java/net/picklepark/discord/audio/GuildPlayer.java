package net.picklepark.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.picklepark.discord.adaptor.LavaPlayerInputStreamAdaptor;
import net.picklepark.discord.handler.send.MultichannelPlayerSendHandler;
import net.picklepark.discord.service.AudioPlaybackService;
import net.picklepark.discord.service.impl.AudioPlaybackServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import static net.dv8tion.jda.api.audio.AudioReceiveHandler.OUTPUT_FORMAT;

public class GuildPlayer {

  private static final Logger logger = LoggerFactory.getLogger(GuildPlayer.class);

  public final AudioPlayer player;
  public final TrackScheduler scheduler;

  private final AudioPlaybackService audioPlaybackService;

  public GuildPlayer(AudioPlayerManager manager) {
    player = manager.createPlayer();
    scheduler = new TrackScheduler(player);
    player.addListener(scheduler);
    audioPlaybackService = new AudioPlaybackServiceImpl();
  }

  public AudioSendHandler getSendHandler() {
    return new MultichannelPlayerSendHandler(audioPlaybackService);
  }

  public void queue(AudioTrack track) {
    logger.info("Queueing");
    try {
      player.playTrack(track);
      audioPlaybackService.setChannelOne(new AudioInputStream(
              new LavaPlayerInputStreamAdaptor(player),
              OUTPUT_FORMAT,
              track.getDuration()
      ));
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }

}
