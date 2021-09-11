package net.picklepark.discord.handler;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.picklepark.discord.service.RecordingService;

import javax.annotation.Nonnull;

public class DemultiplexingHandler implements AudioReceiveHandler {

    private final RecordingService recordingService;
    private boolean error;

    public DemultiplexingHandler(RecordingService recordingService) {
        this.recordingService = recordingService;
        error = false;
    }

    @Override
    public boolean canReceiveUser() {
        return !error;
    }

    @Override
    public void handleUserAudio(@Nonnull UserAudio userAudio) {
        try {
            recordingService.receive(userAudio);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }
    }
}
