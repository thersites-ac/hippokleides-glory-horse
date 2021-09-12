package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("volume")
public class GetVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        int volume = actions.getVolume();
        actions.send("Current volume is " + volume);
    }
}