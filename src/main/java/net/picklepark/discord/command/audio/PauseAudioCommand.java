package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("pause")
@Help(name = "pause", message = "Pause the current track.")
public class PauseAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.pause();
        actions.send("Paused :(");
    }

}
