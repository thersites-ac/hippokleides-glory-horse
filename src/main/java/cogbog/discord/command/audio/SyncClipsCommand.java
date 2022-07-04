package cogbog.discord.command.audio;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.constants.HelpMessages;
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
        // fixme: given that I'm syncing at startup, do I need to do this?
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
        return HelpMessages.SYNC;
    }

    @Override
    public String userInput() {
        return "sync";
    }
}
