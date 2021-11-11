package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.HelpMessages;
import net.picklepark.discord.handler.DemultiplexingHandler;
import net.picklepark.discord.service.RecordingService;

import javax.inject.Inject;

@UserInput("record")
@Help(name = "record", message = HelpMessages.RECORD)
@Auth(Auth.Level.ADMIN)
public class RecordCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public RecordCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(DiscordActions actions) {
        actions.connect();
        recordingService.beginRecording();
        actions.setReceivingHandler(new DemultiplexingHandler(recordingService));
        actions.send("Ready 4 u ;)");
    }

}
