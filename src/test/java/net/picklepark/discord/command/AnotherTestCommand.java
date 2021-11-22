package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;

public class AnotherTestCommand implements DiscordCommand {
    @Override
    public void execute(DiscordActions actions) {
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