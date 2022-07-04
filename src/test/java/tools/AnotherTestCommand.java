package tools;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;

public class AnotherTestCommand implements DiscordCommand {
    @Override
    public void execute(MessageReceivedActions actions) {
        actions.send("OK again");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return "other";
    }

    @Override
    public String helpMessage() {
        return "plz help";
    }

    @Override
    public String userInput() {
        return "another test";
    }
}