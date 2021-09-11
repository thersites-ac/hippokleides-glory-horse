package net.picklepark.discord.handler;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.picklepark.discord.service.RecordingService;

import javax.annotation.Nonnull;

public class CombinedHandler implements AudioReceiveHandler {

    private final RecordingService recordingService;
    private boolean error;

    public CombinedHandler(RecordingService recordingService) {
        this.recordingService = recordingService;
        error = false;
    }

    @Override
    public boolean canReceiveCombined() {
        return error;
    }

    @Override
    public void handleCombinedAudio(@Nonnull CombinedAudio combinedAudio) {
        try {
            recordingService.receive(combinedAudio);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }
    }

}