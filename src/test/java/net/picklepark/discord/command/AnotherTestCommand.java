package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;

@UserInput("another test")
@Help(name = "other", message = "plz help")
public class AnotherTestCommand implements DiscordCommand {
    @Override
    public void execute(DiscordActions actions) {
        actions.send("OK again");
    }
}