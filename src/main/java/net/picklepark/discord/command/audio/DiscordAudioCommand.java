package net.picklepark.discord.command.audio;

import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.command.audio.util.GuildPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class DiscordAudioCommand implements DiscordCommand {

    @Override
    public abstract void execute();

    protected final TextChannel channel;
    protected final GuildPlayer guildPlayer;
    protected final AudioPlayerManager playerManager;

    public DiscordAudioCommand(AudioContext context) {
        this.channel = context.channel;
        this.guildPlayer = context.guildPlayer;
        this.playerManager = context.playerManager;
    }

    protected void acknowledge(String message) {
        channel.sendMessage(message).queue();
    }

}
