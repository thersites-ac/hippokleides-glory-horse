package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.ClipManager;

import javax.inject.Inject;

public class ListCommandsCommand implements DiscordCommand {

    private final ClipManager commandManager;

    @Inject
    public ListCommandsCommand(ClipManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        commandManager.getAllCommandNames()
                .stream()
                .reduce((s, t) -> s + ", " + t)
                .ifPresentOrElse(
                        s -> actions.send("Clips: " + s),
                        () -> actions.send("You have no clips :("));
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
    }

    @Override
    public String example() {
        return "list";
    }

    @Override
    public String helpMessage() {
        return "List all the recorded clips I can play back";
    }

    @Override
    public String userInput() {
        return "list";
    }
}
