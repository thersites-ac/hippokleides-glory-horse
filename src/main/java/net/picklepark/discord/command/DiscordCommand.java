package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public interface DiscordCommand {
    void execute(MessageReceivedActions actions) throws DiscordCommandException;
    AuthLevel requiredAuthLevel();
    String example();
    String helpMessage();
    String userInput();
}
