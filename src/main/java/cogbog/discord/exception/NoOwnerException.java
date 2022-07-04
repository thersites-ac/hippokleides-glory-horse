package cogbog.discord.exception;

public class NoOwnerException extends Exception {
    public NoOwnerException(String name) {
        super("Guild " + name + " has no owner");
    }
}
