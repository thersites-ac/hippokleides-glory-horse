package net.picklepark.discord.command.audio.impl;

import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.command.audio.DiscordAudioCommand;

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
