package net.picklepark.discord.handler.receive;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.picklepark.discord.service.RecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static java.lang.String.format;

public class DemultiplexingHandler implements AudioReceiveHandler {

    private static final Logger logger = LoggerFactory.getLogger(DemultiplexingHandler.class);
    private static final String MESSAGE = "While processing %s (%s)";

    private final RecordingService recordingService;
    private boolean error;
    private final String guild;

    public DemultiplexingHandler(String guild, RecordingService recordingService) {
        this.recordingService = recordingService;
        this.guild = guild;
        error = false;
    }

    @Override
    public boolean canReceiveUser() {
        return !error;
    }

    @Override
    public void handleUserAudio(@Nonnull UserAudio userAudio) {
        try {
            recordingService.receive(guild, userAudio);
        } catch (Exception e) {
            String userId = userAudio.getUser().getId();
            String userTag = userAudio.getUser().getAsTag();
            logger.error(format(MESSAGE, userTag, userId), e);
            error = true;
        }
    }
}
