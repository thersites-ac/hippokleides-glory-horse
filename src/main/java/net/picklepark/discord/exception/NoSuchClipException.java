package net.picklepark.discord.exception;

import static java.lang.String.format;

public class NoSuchClipException extends Exception {
    public NoSuchClipException(String guild, String title) {
        super(format("clip %s/%s not found", guild, title));
    }
}
