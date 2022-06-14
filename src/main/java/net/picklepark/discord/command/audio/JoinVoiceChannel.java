package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.service.RemoteStorageService;

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
