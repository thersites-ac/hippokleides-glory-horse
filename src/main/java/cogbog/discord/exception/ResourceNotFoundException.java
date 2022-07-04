package cogbog.discord.exception;

public class ResourceNotFoundException extends Exception {
  private static final String format = "#%s not found in %s";

  public ResourceNotFoundException(String id, String uri) {
    super(String.format(format, id, uri));
  }

  public ResourceNotFoundException() {
    super();
  }
}
