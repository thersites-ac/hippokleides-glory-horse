package net.picklepark.discord.exception;

public class NotFoundException extends RuntimeException {
  private static final String format = "#%s not found in %s";

  public NotFoundException(String id, String uri) {
    super(String.format(format, id, uri));
  }
}
