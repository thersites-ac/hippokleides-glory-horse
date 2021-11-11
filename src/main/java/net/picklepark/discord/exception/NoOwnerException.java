package net.picklepark.discord.exception;

public class NoOwnerException extends Throwable {
    public NoOwnerException(String name) {
        super("Channel " + name + " has no owner");
    }
}
