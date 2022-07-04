package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.exception.UserIdentificationException;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.model.LocalClip;
import cogbog.discord.service.ClipManager;
import cogbog.discord.service.WelcomeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class WelcomeCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeCommand.class);

    private static final String HELP_MESSAGE = "Tell me to play a clip whenever a user joins audio chat";
    private static final String USER = "user";
    private static final String CLIP = "clip";
    private static final String INPUT_STRING = String.format("welcome <%s> with <%s>", USER, CLIP);

    private final WelcomeManager welcomeManager;
    private final ClipManager clipManager;

    @Inject
    public WelcomeCommand(WelcomeManager welcomeManager, ClipManager clipManager) {
        this.welcomeManager = welcomeManager;
        this.clipManager = clipManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String guild = actions.getGuildId();
        String user = actions.getArgument(USER);
        try {
            long userId = actions.lookupUserId(user);
            String title = actions.getArgument(CLIP);
            PlayClipCommand command = clipManager.lookup(actions.getGuildId(), title);
            if (command != null) {
                String path = command.getPath();
                LocalClip clip = LocalClip.builder()
                        .title(title)
                        .guild(actions.getGuildId())
                        .path(path)
                        .build();
                welcomeManager.set(userId, guild, clip);
                actions.send("Let the greetings commence!");
            } else {
                actions.send("I've never heard of " + title);
            }
        } catch (IOException e) {
            logger.error("While setting welcome for " + user + " in " + guild, e);
            throw new DiscordCommandException(e);
        } catch (UserIdentificationException e) {
            logger.error("Unidentified user " + user + " in channel " + guild, e);
            actions.send("I don't know who " + user + " is");
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return INPUT_STRING;
    }

    @Override
    public String helpMessage() {
        return HELP_MESSAGE;
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }
}
