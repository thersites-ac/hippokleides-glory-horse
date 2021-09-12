package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("skip")
@SuccessMessage("Skipped to the next track.")
public class SkipAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.skip();
    }

}