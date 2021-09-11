package net.picklepark.discord.command.audio;

import net.dv8tion.jda.api.entities.TextChannel;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class SkipAudioCommand extends DiscordAudioCommand {

    public SkipAudioCommand(AudioContext context) {
        super(context);
    }

    @Override
    public void execute(DiscordActions actions) {
        guildPlayer.scheduler.nextTrack();
        actions.send("Skipped to next track.");
    }
}
