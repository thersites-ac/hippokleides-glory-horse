package net.picklepark.discord.exception;

public class DiscordCommandException extends Exception {
    public DiscordCommandException(Exception e) {
        super(e);
    }
}
