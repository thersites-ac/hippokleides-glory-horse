package net.picklepark.discord.exception;

public class CannotFindUserException extends Exception {
    public CannotFindUserException(String user) {
        super(user);
    }
}
