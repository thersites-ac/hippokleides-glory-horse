package cogbog.discord.command.general;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.UnimplementedException;

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