package net.picklepark.discord.command.audio.util;

import net.picklepark.discord.command.audio.util.GuildPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;

public class AudioContext {
    public final TextChannel channel;
    public final GuildPlayer guildPlayer;
    public final AudioPlayerManager playerManager;

    public AudioContext(TextChannel channel, GuildPlayer guildPlayer, AudioPlayerManager playerManager) {
        this.channel = channel;
        this.guildPlayer = guildPlayer;
        this.playerManager = playerManager;
        channel.getGuild().getAudioManager().setSendingHandler(guildPlayer.getSendHandler());
    }
}