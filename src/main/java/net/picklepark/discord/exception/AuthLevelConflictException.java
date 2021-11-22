package net.picklepark.discord.exception;

public class AuthLevelConflictException extends Exception {
    public AuthLevelConflictException(long i) {
        super("For id: " + i);
    }
}
