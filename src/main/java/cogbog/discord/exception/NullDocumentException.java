package cogbog.discord.exception;

public class NullDocumentException extends RuntimeException {
    public NullDocumentException(String url) {
        super("For url: " + url);
    }
}
