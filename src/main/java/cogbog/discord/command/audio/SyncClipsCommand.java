package cogbog.discord.command.audio;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.service.RemoteStorageService;

import javax.inject.Inject;

public class SyncClipsCommand implements DiscordCommand {

    private final RemoteStorageService storageService;

    @Inject
    public SyncClipsCommand(RemoteStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        storageService.sync(actions.getGuildId());
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
        return "Download the latest clips. Use this if you think I'm missing any, but I'm generally pretty good at staying up to date";
    }

    @Override
    public String userInput() {
        return "sync";
    }
}
