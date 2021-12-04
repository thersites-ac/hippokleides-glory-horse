package net.picklepark.discord.tools;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public class SilentCommand extends SpyCommand {
    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        super.execute(actions);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
    }

    @Override
    public String userInput() {
        return "silent";
    }
}