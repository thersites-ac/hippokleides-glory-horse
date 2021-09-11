package net.picklepark.discord.exception;

public class NoSuchUserException extends Exception {
    public NoSuchUserException(String user) {
        super(user);
    }
}
