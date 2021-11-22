package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public class UnadminCommand implements DiscordCommand {
    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        // TODO
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.OWNER;
    }

    @Override
    public String example() {
        return "unadmin <user>";
    }

    @Override
    public String helpMessage() {
        return "Revoke admin privileges";
    }

    @Override
    public String userInput() {
        return "unadmin (?<user>.+)";
    }
}
