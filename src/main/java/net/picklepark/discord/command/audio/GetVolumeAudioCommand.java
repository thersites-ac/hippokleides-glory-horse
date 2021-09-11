package net.picklepark.discord.command.audio;

import net.picklepark.discord.command.audio.DiscordAudioCommand;
import net.picklepark.discord.command.audio.util.AudioContext;

public class GetVolumeAudioCommand extends DiscordAudioCommand {

    public GetVolumeAudioCommand(AudioContext context) {
        super(context);
    }

    @Override
    public void execute() {
        int volume = guildPlayer.player.getVolume();
        acknowledge("Current volume is " + volume);
    }
}
