package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;

public class ChangeVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        try {
            int volume = Integer.parseInt(actions.getArgument("volume"));
            actions.setVolume(volume);
            actions.send("Changed!");
        } catch (NumberFormatException ex) {
            actions.send("Do you know what integers are? Really, " + actions.getArgument("volume") + "?");
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "volume <n>";
    }

    @Override
    public String helpMessage() {
        return "Set the volume.";
    }

    @Override
    public String userInput() {
        return "volume (?<volume>.+)";
    }

}
