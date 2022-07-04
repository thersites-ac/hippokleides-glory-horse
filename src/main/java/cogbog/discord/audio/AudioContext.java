package cogbog.discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

public class AudioContext {
    public final AudioManager audioManager;
    public final GuildPlayer guildPlayer;
    public final AudioPlayerManager playerManager;

    public AudioContext(Guild guild, GuildPlayer guildPlayer, AudioPlayerManager playerManager) {
        this.audioManager = guild.getAudioManager();
        this.guildPlayer = guildPlayer;
        this.playerManager = playerManager;
        audioManager.setSendingHandler(guildPlayer.getSendHandler());
    }
}