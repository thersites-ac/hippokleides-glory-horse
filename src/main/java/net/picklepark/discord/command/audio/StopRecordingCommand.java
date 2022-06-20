package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.RecordingService;

import javax.inject.Inject;

public class StopRecordingCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public StopRecordingCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        recordingService.stopRecording();
        actions.send("Can't hear a thing");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "stop recording";
    }

    @Override
    public String helpMessage() {
        return "I'll stop recording so you can have private chat time";
    }

    @Override
    public String userInput() {
        return "stop recording";
    }
}
