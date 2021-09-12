package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput(".*")
public class NoopCommand implements DiscordCommand {
    @Override
    public void execute(DiscordActions actions) {
        actions.send("I don't know how to " + actions.userInput());
    }
}