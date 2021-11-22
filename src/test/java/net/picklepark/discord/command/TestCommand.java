package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public class TestCommand extends SpyCommand {

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        actions.send("OK");
        super.execute(actions);
    }

    @Override
    public String userInput() {
        return "test";
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
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