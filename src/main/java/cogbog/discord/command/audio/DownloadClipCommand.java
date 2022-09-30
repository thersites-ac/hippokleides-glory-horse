package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.ClipManager;

import javax.inject.Inject;
import java.io.File;

import static java.lang.String.format;

public class DownloadClipCommand implements DiscordCommand {

    public static final String ARG_CLIP = "clip";

    private static final String DSL = format("download <%s>", ARG_CLIP);
    private static final String HELP_MESSAGE = "I'll put a clip in the chat so you can download it";

    private final ClipManager clipManager;

    @Inject
    public DownloadClipCommand(ClipManager clipManager) {
        this.clipManager = clipManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String guild = actions.getGuildId();
        String title = actions.getArgument(ARG_CLIP);
        // fixme: this is kind of convoluted
        var almostClip = clipManager.lookup(guild, title);
        if (almostClip == null) {
            actions.respond("I don't know any clip by the name of " + title);
        } else {
            var path = almostClip.getPath();
            actions.respond(new File(path), title + ".wav");
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return DSL;
    }

    @Override
    public String helpMessage() {
        return HELP_MESSAGE;
    }

    @Override
    public String userInput() {
        return DSL;
    }
}
