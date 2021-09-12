package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("pause")
@SuccessMessage("Paused :(")
public class PauseAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.pause();
    }

}
