package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;

public class PauseAudioCommand implements DiscordCommand {

    @Override
    public void execute(MessageReceivedActions actions) {
        actions.pause();
        actions.send("Paused :(");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "pause";
    }

    @Override
    public String helpMessage() {
        return "Pause the current track.";
    }

    @Override
    public String userInput() {
        return "pause";
    }

}
