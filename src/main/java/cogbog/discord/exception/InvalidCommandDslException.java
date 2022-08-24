package cogbog.discord.exception;

public class InvalidCommandDslException extends RuntimeException {
    public InvalidCommandDslException(String dsl) {
        super(dsl);
    }
}
