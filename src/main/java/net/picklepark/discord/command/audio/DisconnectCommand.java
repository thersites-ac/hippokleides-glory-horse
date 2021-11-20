package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.service.RecordingService;

import javax.inject.Inject;

@UserInput("gtfo")
@Help(name = "gtfo", message = "Tell me to leave your audio channel.")
@Auth(Auth.Level.ADMIN)
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

}
