package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("volume (?<volume>.+)")
@SuccessMessage("Changed!")
public class ChangeVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        try {
            int volume = Integer.parseInt(actions.getArgument("volume"));
            actions.setVolume(volume);
        } catch (NumberFormatException ex) {
            actions.send("Do you know what integers are? Really, " + actions.getArgument("volume") + "?");
        }
    }

}
