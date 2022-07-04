package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.RecordingService;

import javax.inject.Inject;

public class DisconnectCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public DisconnectCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(MessageReceivedActions actions) {
        actions.disconnect();
        recordingService.stopRecording(actions.getGuildId());
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
