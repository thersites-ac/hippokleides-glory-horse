package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;
import net.picklepark.discord.exception.UnimplementedException;
import net.picklepark.discord.service.RemoteStorageService;

public class PlayClipCommand extends JoinVoiceChannel implements net.picklepark.discord.command.DiscordCommand {

    private static final String TOO_MANY_CLIPS_QUEUED_EXCEPTION = "Stop it, I'm just one bot!";
    private final String path;

    public PlayClipCommand(RemoteStorageService storageService, String path) {
        super(storageService);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void execute(MessageReceivedActions actions) {
        ensureConnected(actions);
        actions.connect();
        try {
            actions.queue(path);
        } catch (NotEnoughQueueCapacityException ex) {
            actions.send(TOO_MANY_CLIPS_QUEUED_EXCEPTION);
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        throw new UnimplementedException();
    }

    @Override
    public String helpMessage() {
        throw new UnimplementedException();
    }

    @Override
    public String userInput() {
        return ".*";
    }
}
