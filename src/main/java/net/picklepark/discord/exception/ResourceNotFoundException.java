package net.picklepark.discord.exception;

public class ResourceNotFoundException extends RuntimeException {
  private static final String format = "#%s not found in %s";

  public ResourceNotFoundException(String id, String uri) {
    super(String.format(format, id, uri));
  }
}
