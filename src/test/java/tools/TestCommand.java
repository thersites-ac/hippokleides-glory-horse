package tools;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.DiscordCommandException;

public class TestCommand extends SpyCommand {

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        actions.send("OK");
        super.execute(actions);
    }

    @Override
    public String userInput() {
        return "test";
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return "test";
    }

    @Override
    public String helpMessage() {
        return "halp";
    }
}