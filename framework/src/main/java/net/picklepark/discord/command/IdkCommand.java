package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.UnimplementedException;

public class IdkCommand implements DiscordCommand {

    @Override
    public void execute(DiscordActions actions) {
        actions.send("I don't know how to " + actions.userInput());
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
    }

    @Override
    public String example() {
        throw new UnimplementedException();
    }

    @Override
    public String helpMessage() {
        throw new UnimplementedException();
    }

    @Override
    public String userInput() {
        return ".*";
    }
}
