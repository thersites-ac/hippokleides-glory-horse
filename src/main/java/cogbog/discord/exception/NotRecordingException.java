package cogbog.discord.exception;

import static java.lang.String.format;

public class NotRecordingException extends Exception {

    private static final String MESSAGE = "Not recording guild %s";

    public NotRecordingException(String guild) {
        super(format(MESSAGE, guild));
    }
}
