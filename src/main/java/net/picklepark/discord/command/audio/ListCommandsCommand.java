package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.DynamicCommandManager;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Consumer;

@UserInput("list")
public class ListCommandsCommand implements DiscordCommand {

    private final DynamicCommandManager commandManager;

    @Inject
    public ListCommandsCommand(DynamicCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        commandManager.getAllCommandNames()
                .stream()
                .reduce((s, t) -> s + ", " + t)
                .ifPresentOrElse(
                        s -> actions.send("Clips: " + s),
                        () -> actions.send("You have no clips :("));
    }
}
