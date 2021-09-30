package net.picklepark.discord.exception;

public class NoSuchUserException extends Exception {

    private final String user;

    public NoSuchUserException(String user) {
        super(user);
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
