package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.model.AuthLevel;

public class UnbanCommand implements DiscordCommand {
    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {

    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return null;
    }

    @Override
    public String example() {
        return null;
    }

    @Override
    public String helpMessage() {
        return null;
    }

    @Override
    public String userInput() {
        return null;
    }
}
