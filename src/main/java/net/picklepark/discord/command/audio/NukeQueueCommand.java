package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public class NukeQueueCommand implements DiscordCommand {

    public static final String CONFIRMATION_MESSAGE = "Gone. All gone.";

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        actions.nuke();
        actions.send(CONFIRMATION_MESSAGE);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "nuke";
    }

    @Override
    public String helpMessage() {
        return "Removes all queued audio";
    }

    @Override
    public String userInput() {
        return "nuke";
    }
}
