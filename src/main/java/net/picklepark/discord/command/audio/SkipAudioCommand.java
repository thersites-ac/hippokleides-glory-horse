package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;

public class SkipAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
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