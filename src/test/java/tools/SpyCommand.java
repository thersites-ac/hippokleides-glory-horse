package tools;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.exception.UnimplementedException;

public class SpyCommand implements DiscordCommand {
    private boolean executed;

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        this.executed = true;
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
        throw new UnimplementedException();
    }

    public boolean isExecuted() {
        return executed;
    }
}
