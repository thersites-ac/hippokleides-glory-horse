package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.ClipCommand;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.ClipManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomClipCommand implements DiscordCommand {

    private static final Random RANDOM = new Random();
    private static final Logger logger = LoggerFactory.getLogger(RandomClipCommand.class);

    private final ClipManager clipManager;

    @Inject
    public RandomClipCommand(ClipManager clipManager) {
        this.clipManager = clipManager;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        try {
            String name = lookupRandomName();
            ClipCommand pickedClip = clipManager.lookup(name);
            actions.send("Prepare to get \"" + name + "\"'d.");
            pickedClip.execute(actions);
        } catch (IllegalArgumentException ex) {
            logger.warn("Channel {} has no recorded clips to randomly select from", actions.getGuildName());
            actions.send("I have never heard the sweet sound of your conversation.");
        }
    }

    private String lookupRandomName() {
        List<String> names = new ArrayList<>(clipManager.getAllCommandNames());
        int randomIndex = RANDOM.nextInt(names.size());
        return names.get(randomIndex);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
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
