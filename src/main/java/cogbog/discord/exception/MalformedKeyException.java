package cogbog.discord.exception;

public class MalformedKeyException extends Exception {
    public MalformedKeyException(String awsKey) {
        super(awsKey);
    }
}
