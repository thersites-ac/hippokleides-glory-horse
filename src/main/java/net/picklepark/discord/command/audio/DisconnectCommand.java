package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("gtfo")
public class DisconnectCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.disconnect();
    }

}
