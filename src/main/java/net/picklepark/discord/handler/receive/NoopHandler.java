package net.picklepark.discord.handler.receive;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;

public class NoopHandler implements AudioReceiveHandler {
    public static final NoopHandler INSTANCE = new NoopHandler();
    private NoopHandler() {}
}
