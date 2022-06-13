package net.picklepark.discord.adaptor.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.picklepark.discord.audio.AudioContext;
import net.picklepark.discord.audio.GuildPlayer;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioActions {

    public static final int MAX_QUEUE_SIZE = 20000;

    protected final AudioContext audioContext;

    public AudioActions(AudioContext audioContext) {
        this.audioContext = audioContext;
    }


    private static class ResultHandler implements AudioLoadResultHandler {

        private static final Logger logger = LoggerFactory.getLogger(ResultHandler.class);

        private final GuildPlayer guildPlayer;
        private final String uri;

        public ResultHandler(GuildPlayer guildPlayer, String uri) {
            this.guildPlayer = guildPlayer;
            this.uri = uri;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            guildPlayer.queue(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            AudioTrack firstTrack = getFirstTrack(playlist);
            guildPlayer.queue(firstTrack);
        }

        private AudioTrack getFirstTrack(AudioPlaylist playlist) {
            AudioTrack firstTrack = playlist.getSelectedTrack();
            if (firstTrack == null) {
                firstTrack = playlist.getTracks().get(0);
            }
            return firstTrack;
        }

        @Override
        public void noMatches() {
            logger.info("No match for " + uri);
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            logger.error("loadFailed", exception);
        }
    }

    public void addToQueue(String uri) throws NotEnoughQueueCapacityException {
        if (audioContext.guildPlayer.scheduler.size() > MAX_QUEUE_SIZE)
            throw new NotEnoughQueueCapacityException("Max size: " + MAX_QUEUE_SIZE);
        audioContext.playerManager.loadItemOrdered(
                audioContext.guildPlayer,
                uri,
                new ResultHandler(audioContext.guildPlayer, uri));
    }
}
