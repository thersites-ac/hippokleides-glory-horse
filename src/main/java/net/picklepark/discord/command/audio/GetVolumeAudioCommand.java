package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("volume")
@Help(name = "volume", message = "Get the current volume.")
@Auth(Auth.Level.ANY)
public class GetVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        int volume = actions.getVolume();
        actions.send("Current volume is " + volume);
    }
}
