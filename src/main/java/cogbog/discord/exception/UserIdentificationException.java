package cogbog.discord.exception;

public class UserIdentificationException extends Exception {

    private final String user;

    public UserIdentificationException(String user) {
        super(user);
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
