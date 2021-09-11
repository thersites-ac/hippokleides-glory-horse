package net.picklepark.discord.command.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.command.audio.util.GuildPlayer;

public abstract class DiscordAudioCommand implements DiscordCommand {

    protected final TextChannel channel;
    protected final GuildPlayer guildPlayer;
    protected final AudioPlayerManager playerManager;

    public DiscordAudioCommand(AudioContext context) {
        this.channel = context.channel;
        this.guildPlayer = context.guildPlayer;
        this.playerManager = context.playerManager;
    }

}
