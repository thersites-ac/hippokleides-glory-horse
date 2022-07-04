package cogbog.discord.exception;

public class NoSuchUserException extends UserIdentificationException {
    public NoSuchUserException(String user) {
        super(user);
    }
}
