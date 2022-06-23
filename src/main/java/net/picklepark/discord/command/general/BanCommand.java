package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.model.AuthLevel;

public class BanCommand implements DiscordCommand {
    private static final String DSL = "ban <user>";
    private static final String HELP_MESSAGE = "Block someone from interacting with me";

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        actions.send("You done goofed, " + actions.getArgument("user"));
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return DSL;
    }

    @Override
    public String helpMessage() {
        return HELP_MESSAGE;
    }

    @Override
    public String userInput() {
        return DSL;
    }
}
