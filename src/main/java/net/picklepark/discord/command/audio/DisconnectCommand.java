package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.service.RecordingService;

import javax.inject.Inject;

public class DisconnectCommand implements DiscordCommand {

    private final RecordingService recordingService;


    @Inject
    public DisconnectCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(DiscordActions actions) {
        actions.disconnect();
        recordingService.stopRecording();
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "gtfo";
    }

    @Override
    public String helpMessage() {
        return "Tell me to leave your audio channel.";
    }

    @Override
    public String userInput() {
        return "gtfo";
    }

}
