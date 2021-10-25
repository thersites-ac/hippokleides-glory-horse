package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.DynamicCommandManager;

@UserInput("list")
public class ListCommandsCommand implements DiscordCommand {

    private final DynamicCommandManager commandManager;

    public ListCommandsCommand(DynamicCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        commandManager.getAllCommandNames();
    }
}
