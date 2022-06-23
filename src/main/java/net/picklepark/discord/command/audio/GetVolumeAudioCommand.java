package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.model.AuthLevel;

public class GetVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(MessageReceivedActions actions) {
        int volume = actions.getVolume();
        actions.send("Current volume is " + volume);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return "volume";
    }

    @Override
    public String helpMessage() {
        return "Get the current volume.";
    }

    @Override
    public String userInput() {
        return "volume";
    }
}
