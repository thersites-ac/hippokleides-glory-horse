package tools;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

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