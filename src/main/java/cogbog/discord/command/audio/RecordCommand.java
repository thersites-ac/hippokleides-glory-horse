package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.handler.receive.DemultiplexingHandler;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.RecordingService;
import cogbog.discord.service.RemoteStorageService;

import javax.inject.Inject;

public class RecordCommand implements DiscordCommand {

    private final RecordingService recordingService;

    @Inject
    public RecordCommand(RecordingService recordingService, RemoteStorageService storageService) {
        this.recordingService = recordingService;
    }

    @Override
    public void execute(MessageReceivedActions actions) {
        if (!actions.isConnected()) {
            actions.connect();
        }
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
        return "listen";
    }

    @Override
    public String helpMessage() {
        return "Begin listening to the voice channel so you can clip it";
    }

    @Override
    public String userInput() {
        return "listen";
    }

}
