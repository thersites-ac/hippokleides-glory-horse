package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.ClipManager;
import cogbog.discord.service.RemoteStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomClipCommand extends JoinVoiceChannel implements DiscordCommand {

    private static final Random RANDOM = new Random();
    private static final Logger logger = LoggerFactory.getLogger(RandomClipCommand.class);

    private final ClipManager clipManager;

    @Inject
    public RandomClipCommand(ClipManager clipManager, RemoteStorageService storageService) {
        super(storageService);
        this.clipManager = clipManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        try {
            String guild = actions.getGuildId();
            String name = lookupRandomName(guild);
            PlayClipCommand pickedClip = clipManager.lookup(guild, name);
            actions.send("Prepare to get \"" + name + "\"'d.");
            pickedClip.execute(actions);
        } catch (IllegalArgumentException ex) {
            logger.warn("Channel {} has no recorded clips to randomly select from", actions.getGuildId());
            actions.send("I have never heard the sweet sound of your conversation.");
        }
    }

    private String lookupRandomName(String guild) {
        List<String> names = new ArrayList<>(clipManager.getAllCommandNames(guild));
        int randomIndex = RANDOM.nextInt(names.size());
        return names.get(randomIndex);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return "random";
    }

    @Override
    public String helpMessage() {
        return "Play a random clip from the recordings I've taken.";
    }

    @Override
    public String userInput() {
        return "random";
    }
}
