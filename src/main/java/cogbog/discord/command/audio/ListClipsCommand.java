package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.ClipManager;
import cogbog.discord.exception.DiscordCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ListClipsCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(ListClipsCommand.class);

    private final ClipManager clipManager;

    @Inject
    public ListClipsCommand(ClipManager clipManager) {
        this.clipManager = clipManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        logger.info("Listing clips for channel " + actions.getGuildId());
        clipManager.getAllCommandNames(actions.getGuildId())
                .stream()
                .sorted()
                .reduce((s, t) -> s + ", " + t)
                .ifPresentOrElse(
                        s -> actions.send("Clips: " + s),
                        () -> actions.send("You have no clips :("));
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return "list";
    }

    @Override
    public String helpMessage() {
        return "List all the recorded clips I can play back";
    }

    @Override
    public String userInput() {
        return "list";
    }
}
