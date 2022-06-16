package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.RemoteStorageService;

import javax.inject.Inject;

public class DeleteClipCommand implements DiscordCommand {

    private static final String INPUT_STRING = "delete <clip>";
    private final ClipManager clipManager;
    private final RemoteStorageService storageService;

    @Inject
    public DeleteClipCommand(ClipManager clipManager, RemoteStorageService storageService) {
        this.clipManager = clipManager;
        this.storageService = storageService;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String clip = actions.getArgument("clip");
        String guild = actions.getGuildId();
        storageService.delete(guild, clip);
        clipManager.delete(guild, clip);
        actions.send("It is gone forever.");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.OWNER;
    }

    @Override
    public String example() {
        return "delete <clip>";
    }

    @Override
    public String helpMessage() {
        return "Delete a clip. WARNING: once you do this it's gone for good!";
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }
}
