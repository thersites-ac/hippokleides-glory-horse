package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;

public class PauseAudioCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.pause();
        actions.send("Paused");
    }

}
