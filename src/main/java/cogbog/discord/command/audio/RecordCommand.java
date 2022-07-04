package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.constants.HelpMessages;
import cogbog.discord.handler.receive.DemultiplexingHandler;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.RecordingService;
import cogbog.discord.service.RemoteStorageService;

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
