package net.picklepark.discord.exception;

import static java.lang.String.format;
import static net.picklepark.discord.constants.AudioConstants.PACKET_SIZE;

public class InvalidAudioPacketException extends Exception {

    private static final String MESSAGE = "Expected %s bytes of audio but received %s";

    public InvalidAudioPacketException(int length) {
        super(format(MESSAGE, PACKET_SIZE, length));
    }
}
