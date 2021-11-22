package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;

public class GetVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        int volume = actions.getVolume();
        actions.send("Current volume is " + volume);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
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
