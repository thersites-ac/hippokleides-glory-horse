package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.ClipManager;

import javax.inject.Inject;
import java.util.Random;

public class RepeatClipCommand implements DiscordCommand {

    public static final String ARGUMENT_NUMBER = "number";
    public static final String ARGUMENT_TITLE = "title";
    public static final String REGEX = String.format("repeat (?<%s>.+) (?<%s>\\S+) times", ARGUMENT_TITLE, ARGUMENT_NUMBER);
    public static final String CONFIRMATION_MESSAGE = "You got it, boss";
    public static final String BAD_NUMBER_INPUT_MESSAGE = "How exactly do you expect me to repeat that %s times?";
    public static final String BAD_CLIP_INPUT_MESSAGE = "I definitely don't know how to %s.";

    private static final Random RANDOM = new Random();

    private final ClipManager clipManager;

    @Inject
    public RepeatClipCommand(ClipManager clipManager) {
        this.clipManager = clipManager;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        String repetitionsInput = actions.getArgument(ARGUMENT_NUMBER);
        try {
            int repetitions = Integer.parseInt(repetitionsInput);
            if (repetitions <= 0)
                throw new NumberFormatException("Negative value for number of repetitions");
            String titleInput = actions.getArgument(ARGUMENT_TITLE);
            PlayClipCommand foundClip = clipManager.lookup(titleInput);
            if (foundClip == null)
                actions.send(String.format(BAD_CLIP_INPUT_MESSAGE, titleInput));
            else {
                for (int i = 0; i < repetitions; i++)
                    foundClip.execute(actions);
                actions.send(CONFIRMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            actions.send(String.format(BAD_NUMBER_INPUT_MESSAGE, repetitionsInput));
        } catch (Exception ex) {
            throw new DiscordCommandException(ex);
        }


    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "repeat 42 wow";
    }

    @Override
    public String helpMessage() {
        return "Repeats a clip as many times as you want";
    }

    @Override
    public String userInput() {
        return REGEX;
    }
}
