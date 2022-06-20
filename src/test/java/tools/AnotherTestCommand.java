package tools;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.model.AuthLevel;

public class AnotherTestCommand implements DiscordCommand {
    @Override
    public void execute(MessageReceivedActions actions) {
        actions.send("OK again");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
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