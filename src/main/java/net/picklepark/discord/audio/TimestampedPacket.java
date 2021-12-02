package net.picklepark.discord.audio;

import java.util.Arrays;

public class TimestampedPacket {
    public static final byte[] SILENCE_DATA = new byte[DiscontinuousAudioArray.PACKET_SIZE];

    public final boolean isSilence;
    public final long timestamp;
    public final byte[] data;

    static {
        Arrays.fill(SILENCE_DATA, (byte) 0);
    }

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
