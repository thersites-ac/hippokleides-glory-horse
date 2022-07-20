package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.NotEnoughQueueCapacityException;
import cogbog.discord.model.AuthLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// fixme: send an appropriate message and log a warning when the user tries to queue an invalid URI
public class QueueAudioCommand implements DiscordCommand {

    public static final String ARGUMENT = "uri";
    public static final String CONFIRMATION_MESSAGE = "I CANNOT WAIT TO PLAY THIS SONG";

    private static final Logger logger = LoggerFactory.getLogger(QueueAudioCommand.class);
    private static final String WAIT_A_BIT_MESSAGE = "Wait out the current track of skip it, then try again.";
    private static final String INPUT_STRING = "queue <uri>";

    @Override
    public void execute(MessageReceivedActions actions) {
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
        return "Add a track to the playlist";
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }

}
