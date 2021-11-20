package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.RecordingService;

import javax.inject.Inject;

@UserInput("stop recording")
@Auth(Auth.Level.ADMIN)
@Help(name = "stop recording", message = "I'll stop recording so you can have private chat time")
public class StopRecordingCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public StopRecordingCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        recordingService.stopRecording();
    }
}
