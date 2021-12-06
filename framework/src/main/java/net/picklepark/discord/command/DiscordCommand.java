package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public interface DiscordCommand {
    void execute(DiscordActions actions) throws DiscordCommandException;
    AuthLevel requiredAuthLevel();
    String example();
    String helpMessage();
    String userInput();
}
