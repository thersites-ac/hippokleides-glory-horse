package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("gtfo")
@Help(name = "gtfo", message = "Tell me to leave your audio channel.")
public class DisconnectCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.disconnect();
    }

}
