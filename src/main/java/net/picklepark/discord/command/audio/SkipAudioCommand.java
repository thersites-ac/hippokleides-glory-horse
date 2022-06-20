package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.model.AuthLevel;

public class SkipAudioCommand implements DiscordCommand {

    @Override
    public void execute(MessageReceivedActions actions) {
        actions.skip();
        actions.send("Skipped to the next track.");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "skip";
    }

    @Override
    public String helpMessage() {
        return "Skip a track.";
    }

    @Override
    public String userInput() {
        return "skip";
    }

}