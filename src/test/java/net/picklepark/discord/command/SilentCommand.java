package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.exception.DiscordCommandException;

@UserInput("silent")
@Auth(Auth.Level.ANY)
public class SilentCommand extends SpyCommand {
    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        super.execute(actions);
    }
}