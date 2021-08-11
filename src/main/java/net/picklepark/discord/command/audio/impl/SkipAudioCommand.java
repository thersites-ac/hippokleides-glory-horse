package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.dv8tion.jda.api.entities.TextChannel;

public class SkipAudioCommand extends DiscordAudioCommand {

    private final TextChannel channel;

    public SkipAudioCommand(AudioContext context) {
        super(context);
        this.channel = context.channel;
        acknowledge("Skipping!");
    }

    @Override
    public void execute() {
        guildPlayer.scheduler.nextTrack();
        channel.sendMessage("Skipped to next track.").queue();
    }
}
