package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.service.RemoteStorageService;

public class JoinVoiceChannel {
    private final RemoteStorageService storageService;

    public JoinVoiceChannel(RemoteStorageService storageService) {
        this.storageService = storageService;
    }

    public void ensureConnected(MessageReceivedActions actions) {
        if (!actions.isConnected()) {
            actions.connect();
            storageService.sync(actions.getGuildId());
        }
    }
}
