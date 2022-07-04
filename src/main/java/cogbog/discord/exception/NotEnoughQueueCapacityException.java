package cogbog.discord.exception;

public class NotEnoughQueueCapacityException extends Exception {
    public NotEnoughQueueCapacityException(String message) {
        super(message);
    }
}
