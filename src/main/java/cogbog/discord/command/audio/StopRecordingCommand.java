package cogbog.discord.command.audio;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.handler.receive.NoopHandler;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.service.RecordingService;

import javax.inject.Inject;

public class StopRecordingCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public StopRecordingCommand(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        recordingService.stopRecording(actions.getGuildId());
        actions.setReceivingHandler(NoopHandler.INSTANCE);
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
