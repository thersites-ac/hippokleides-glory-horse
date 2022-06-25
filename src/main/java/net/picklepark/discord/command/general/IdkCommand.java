package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.exception.UnimplementedException;

public class IdkCommand implements DiscordCommand {

    @Override
    public void execute(MessageReceivedActions actions) {
        actions.send("I don't know how to " + actions.getArgument("command"));
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
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
        return "<command>";
    }
}