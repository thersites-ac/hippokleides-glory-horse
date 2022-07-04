package cogbog.discord.exception;

public class AuthLevelConflictException extends AuthException {
    public AuthLevelConflictException(long i) {
        super("For id: " + i);
    }
}
