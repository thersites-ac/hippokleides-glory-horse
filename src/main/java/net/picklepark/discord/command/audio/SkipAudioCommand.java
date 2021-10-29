package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("skip")
@Help(name = "skip", message = "Skip a track.")
public class SkipAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.skip();
        actions.send("Skipped to the next track.");
    }

}