package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.constants.HelpMessages;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.RemoteStorageService;

import javax.inject.Inject;

public class SyncClipsCommand implements DiscordCommand {

    private final RemoteStorageService storageService;

    @Inject
    public SyncClipsCommand(RemoteStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        storageService.sync();
        actions.send("Got 'em all");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "sync";
    }

    @Override
    public String helpMessage() {
        return HelpMessages.SYNC;
    }

    @Override
    public String userInput() {
        return "sync";
    }
}
