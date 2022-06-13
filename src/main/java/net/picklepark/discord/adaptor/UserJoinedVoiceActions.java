package net.picklepark.discord.adaptor;

import net.picklepark.discord.exception.NotEnoughQueueCapacityException;

public interface UserJoinedVoiceActions {
    String user();
    String channel();
    boolean isConnected();
    void play(String path) throws NotEnoughQueueCapacityException;
}
