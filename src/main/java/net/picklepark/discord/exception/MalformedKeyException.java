package net.picklepark.discord.exception;

public class MalformedKeyException extends Throwable {
    public MalformedKeyException(String awsKey) {
        super(awsKey);
    }
}
