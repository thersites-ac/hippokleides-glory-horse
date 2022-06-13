package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.constants.HelpMessages;
import net.picklepark.discord.handler.receive.DemultiplexingHandler;
import net.picklepark.discord.service.RecordingService;

import javax.inject.Inject;

public class RecordCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public RecordCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(MessageReceivedActions actions) {
        actions.connect();
        recordingService.beginRecording();
        actions.setReceivingHandler(new DemultiplexingHandler(recordingService));
        actions.send("Ready 4 u ;)");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "record";
    }

    @Override
    public String helpMessage() {
        return HelpMessages.RECORD;
    }

    @Override
    public String userInput() {
        return "record";
    }

}
