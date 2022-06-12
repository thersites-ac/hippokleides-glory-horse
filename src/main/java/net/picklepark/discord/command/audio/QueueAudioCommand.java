package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueAudioCommand implements DiscordCommand {

    public static final String ARGUMENT = "uri";
    public static final String CONFIRMATION_MESSAGE = "I CANNOT WAIT TO PLAY THIS SONG";

    private static final Logger logger = LoggerFactory.getLogger(QueueAudioCommand.class);
    private static final String WAIT_A_BIT_MESSAGE = "Wait out the current track of skip it, then try again.";

    @Override
    public void execute(DiscordActions actions) {
        String uri = actions.getArgument(ARGUMENT);
        actions.connect();
        try {
            actions.queue(uri);
            actions.send(CONFIRMATION_MESSAGE);
        } catch (NotEnoughQueueCapacityException ex) {
            actions.send(WAIT_A_BIT_MESSAGE);
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "queue <url>";
    }

    @Override
    public String helpMessage() {
        return "Add a track to the playlist.";
    }

    @Override
    public String userInput() {
        return "queue (?<uri>.+)";
    }

}
