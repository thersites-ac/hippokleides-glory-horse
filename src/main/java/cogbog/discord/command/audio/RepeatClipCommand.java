package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.adaptor.impl.AudioActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.ClipManager;
import cogbog.discord.exception.NotEnoughQueueCapacityException;

import javax.inject.Inject;

public class RepeatClipCommand implements DiscordCommand {

    public static final String ARGUMENT_NUMBER = "number";
    public static final String ARGUMENT_TITLE = "title";
    public static final String INPUT_STRING = String.format("repeat <%s> <%s> times", ARGUMENT_TITLE, ARGUMENT_NUMBER);
    public static final String CONFIRMATION_MESSAGE = "Buckle up.";
    public static final String BAD_NUMBER_INPUT_MESSAGE = "How exactly do you expect me to repeat that %s times?";
    public static final String BAD_CLIP_INPUT_MESSAGE = "I definitely don't know how to %s.";
    public static final String INSUFFICIENT_QUEUE_SPACE = "No, that's a stupid number.";

    private final ClipManager clipManager;

    @Inject
    public RepeatClipCommand(ClipManager clipManager) {
        this.clipManager = clipManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        if (!actions.isConnected()) {
            actions.connect();
        }
        String repetitionsInput = actions.getArgument(ARGUMENT_NUMBER);
        try {
            int repetitions = Integer.parseInt(repetitionsInput);
            int capacity = AudioActions.MAX_QUEUE_SIZE - actions.getAudioQueueSize();
            if (capacity < repetitions)
                throw new NotEnoughQueueCapacityException(capacity + "");
            else if (repetitions <= 0)
                throw new NumberFormatException("Negative value for number of repetitions");
            String titleInput = actions.getArgument(ARGUMENT_TITLE);
            PlayClipCommand foundClip = clipManager.lookup(actions.getGuildId(), titleInput);
            if (foundClip == null)
                actions.send(String.format(BAD_CLIP_INPUT_MESSAGE, titleInput));
            else {
                for (int i = 0; i < repetitions; i++)
                    foundClip.execute(actions);
                actions.send(CONFIRMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            actions.send(String.format(BAD_NUMBER_INPUT_MESSAGE, repetitionsInput));
        } catch (NotEnoughQueueCapacityException ex) {
            actions.send(INSUFFICIENT_QUEUE_SPACE);
        } catch (Exception ex) {
            throw new DiscordCommandException(ex);
        }


    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return INPUT_STRING;
    }

    @Override
    public String helpMessage() {
        return "Repeats a clip as many times as you want";
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }
}
