package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public interface DiscordCommand {
    void execute(MessageReceivedActions actions) throws DiscordCommandException;
    AuthLevel requiredAuthLevel();
    // todo: I can generally replace this with the DSL pattern now
    String example();
    String helpMessage();
    String userInput();
}
