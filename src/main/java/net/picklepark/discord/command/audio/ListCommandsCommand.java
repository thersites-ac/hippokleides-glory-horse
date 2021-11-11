package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.ClipManager;

import javax.inject.Inject;

@UserInput("list")
@Help(name = "list", message = "List all the recorded clips I can play back.")
@Auth(Auth.Level.ANY)
public class ListCommandsCommand implements DiscordCommand {

    private final ClipManager commandManager;

    @Inject
    public ListCommandsCommand(ClipManager commandManager) {
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
