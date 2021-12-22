package net.picklepark.discord.handler.send;

import com.google.inject.internal.asm.$ModuleVisitor;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.picklepark.discord.service.AudioPlaybackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MultichannelPlayerSendHandler implements AudioSendHandler {

    private static final Logger logger = LoggerFactory.getLogger(MultichannelPlayerSendHandler.class);

    private final AudioPlaybackService playbackService;

    @Inject
    public MultichannelPlayerSendHandler(AudioPlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    @Override
    public boolean canProvide() {
        try {
            return playbackService.hasNext();
        } catch (IOException e) {
            logger.error("While checking upcoming data from AudioPlaybackService", e);
            return false;
        }
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        try {
            return ByteBuffer.wrap(playbackService.nextTwentyMs());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}