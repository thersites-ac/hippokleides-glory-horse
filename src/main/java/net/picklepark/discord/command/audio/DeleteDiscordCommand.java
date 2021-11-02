package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.RemoteStorageService;

import javax.inject.Inject;

@UserInput("delete (?<clip>.*)")
@Help(name = "delete <clip>", message = "Delete a clip. WARNING: once you do this it's gone for good!")
public class DeleteDiscordCommand implements DiscordCommand {

    private final ClipManager commandManager;
    private final RemoteStorageService storageService;

    @Inject
    public DeleteDiscordCommand(ClipManager commandManager, RemoteStorageService storageService) {
        this.commandManager = commandManager;
        this.storageService = storageService;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        String clip = actions.getArgument("clip");
        storageService.delete(clip);
        commandManager.delete(clip);
        actions.send("It is gone forever.");
    }
}
