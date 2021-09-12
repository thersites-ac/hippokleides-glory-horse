package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.handler.DemultiplexingHandler;
import net.picklepark.discord.service.RecordingService;

import javax.inject.Inject;

@UserInput("record")
@SuccessMessage("Ready 4 u ;)")
public class RecordCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public RecordCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(DiscordActions actions) {
        actions.connect();
        if (recordingService.isRecording())
            actions.send("I'm already recording. Leave me alone.");
        else {
            recordingService.beginRecording();
            actions.setReceivingHandler(new DemultiplexingHandler(recordingService));
        }
    }

}
