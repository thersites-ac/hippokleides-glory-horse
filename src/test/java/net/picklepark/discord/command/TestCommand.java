package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.exception.DiscordCommandException;

@UserInput("test")
@Help(name = "test", message = "halp")
@Auth(Auth.Level.ANY)
public class TestCommand extends SpyCommand {

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        actions.send("OK");
        super.execute(actions);
    }

}