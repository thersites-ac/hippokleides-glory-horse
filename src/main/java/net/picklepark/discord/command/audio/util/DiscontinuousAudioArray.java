package net.picklepark.discord.command.audio.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;

public class DiscontinuousAudioArray {

    public static final int BYTES_PER_MS = 192;
    public static final int MS_PER_PACKET = 20;
    public static final int PACKET_SIZE = BYTES_PER_MS * MS_PER_PACKET;
    private final long maxInterval;

    private Logger logger = LoggerFactory.getLogger(DiscontinuousAudioArray.class);

    private final LinkedList<TimestampedPacket> audio;

    public DiscontinuousAudioArray() {
        audio = new LinkedList<>();
        maxInterval = 60000L;
    }

    public DiscontinuousAudioArray(long recordDuration) {
        this.maxInterval = recordDuration;
        audio = new LinkedList<>();
    }

    public byte[] retrieve() {
        return new PacketInterpolator(audio).interpolate();
    }

    public void store(byte[] data) {
        removeOldData();
        if (isSilence(data))
            audio.add(TimestampedPacket.silence());
        else
            audio.add(new TimestampedPacket(data));
    }

    private void removeOldData() {
        long now = System.currentTimeMillis();
        while (firstPacketIsOldFor(now))
            audio.removeFirst();
    }

    private boolean firstPacketIsOldFor(long now) {
        if (audio.isEmpty())
            return false;
        return now - audio.getFirst().timestamp > maxInterval;
    }

    private boolean isSilence(byte[] data) {
        return Arrays.equals(data, TimestampedPacket.SILENCE_DATA);
    }

    public static class TimestampedPacket {
        public static final byte[] SILENCE_DATA = new byte[PACKET_SIZE];

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

}
