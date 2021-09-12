package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Catches;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("volume (?<volume>.+)")
@SuccessMessage("Changed!")
public class ChangeVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        int volume = Integer.parseInt(actions.getArgument("volume"));
        actions.setVolume(volume);
    }

    @Catches(NumberFormatException.class)
    public void badFormat(DiscordActions actions) {
        actions.send("Do you know what integers are? Really, " + actions.getArgument("volume") + "?");
    }



}
