package net.picklepark.discord.audio;

import static net.picklepark.discord.constants.AudioConstants.SILENCE_DATA;

public class TimestampedPacket {

    public final boolean isSilence;
    public final long timestamp;
    public final byte[] data;

    public TimestampedPacket(byte[] data) {
        this.data = data;
        timestamp = System.currentTimeMillis();
        isSilence = false;
    }

    public TimestampedPacket(byte[] data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
        isSilence = false;
    }

    private TimestampedPacket() {
        this.isSilence = true;
        this.data = SILENCE_DATA;
        this.timestamp = System.currentTimeMillis();
    }

    public static TimestampedPacket silence() {
        return new TimestampedPacket();
    }
}
