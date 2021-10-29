package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.RemoteStorageService;

import javax.inject.Inject;

@UserInput("sync")
@Help(name = "sync", message = "Download the latest clips. Use this if you think I'm missing any, but I'm generally pretty good at staying up to date.")
public class SyncClipsCommand implements DiscordCommand {

    private final RemoteStorageService storageService;

    @Inject
    public SyncClipsCommand(RemoteStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        storageService.sync();
        actions.send("Got 'em all");
    }
}
