package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.AudioContext;
import net.picklepark.discord.command.audio.util.GuildPlayer;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueAudioCommand extends DiscordAudioCommand {

    private final String uri;
    private static final Logger logger = LoggerFactory.getLogger(QueueAudioCommand.class);

    public QueueAudioCommand(String uri, AudioContext context) {
        super(context);
        this.uri = uri;
        ensureConnected(channel.getGuild().getAudioManager());
    }

    @Override
    public void execute() {
        playerManager.loadItemOrdered(guildPlayer, uri, new ResultHandler(guildPlayer));
        acknowledge("Queued " + uri);
    }

    private void ensureConnected(AudioManager audioManager) {
        if (!audioManager.isConnected())
            audioManager.getGuild().getVoiceChannels().stream()
                    .findFirst()
                    .ifPresent(audioManager::openAudioConnection);
    }

    private class ResultHandler implements AudioLoadResultHandler {

        private final GuildPlayer guildPlayer;

        public ResultHandler(GuildPlayer guildPlayer) {
            this.guildPlayer = guildPlayer;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
            guildPlayer.queue(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            AudioTrack firstTrack = getFirstTrack(playlist);
            channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();
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
            channel.sendMessage("Nothing found by " + uri).queue();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            channel.sendMessage("Could not play: " + exception.getMessage()).queue();
        }
    }
}
