package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.constants.HelpMessages;
import net.picklepark.discord.handler.receive.DemultiplexingHandler;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.RemoteStorageService;

import javax.inject.Inject;

public class RecordCommand extends JoinVoiceChannel implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public RecordCommand(RecordingService recordingService, RemoteStorageService storageService) {
        super(storageService);
        this.recordingService = recordingService;
    }

    @Override
    public void execute(MessageReceivedActions actions) {
        ensureConnected(actions);
        String guild = actions.getGuildId();
        recordingService.beginRecording(guild);
        actions.setReceivingHandler(new DemultiplexingHandler(guild, recordingService));
        actions.send("I'm listening!");
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
