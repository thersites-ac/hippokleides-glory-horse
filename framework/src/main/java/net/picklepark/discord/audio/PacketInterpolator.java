package net.picklepark.discord.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.picklepark.discord.audio.DiscontinuousAudioArray.MS_PER_PACKET;
import static net.picklepark.discord.audio.DiscontinuousAudioArray.PACKET_SIZE;
import static net.picklepark.discord.audio.TimestampedPacket.SILENCE_DATA;

public class PacketInterpolator {

    private static final Logger logger = LoggerFactory.getLogger(PacketInterpolator.class);

    private final List<TimestampedPacket> audio;
    private final byte[] everything;

    private long lastSilenceTimestamp;
    private int ptr;
    private int continuousSilences;

    public PacketInterpolator(List<TimestampedPacket> audio) {
        this.audio = audio;
        int unrecordedSilences = countUnrecordedSilences();
        int size = audio.size() + unrecordedSilences;
        everything = new byte[size * PACKET_SIZE];
        lastSilenceTimestamp = -1;
        ptr = 0;
        continuousSilences = 0;
    }

    private int countUnrecordedSilences() {
        int total = 0;
        continuousSilences = 0;
        TimestampedPacket last = null;
        for (TimestampedPacket packet: audio) {
            if (packet.isSilence) {
                continuousSilences++;
                last = packet;
            } else if (continuousSilences >= 5) {
                total += countSilencesInInterval(last.timestamp, packet.timestamp);
                continuousSilences = 0;
            }
        }
        return total;
    }

    private int countSilencesInInterval(long lastTimestamp, long timestamp) {
        return (int) ((timestamp - lastTimestamp) / MS_PER_PACKET);
    }

    public byte[] interpolate() {
        continuousSilences = 0;
        for (TimestampedPacket packet: audio)
            appendPacket(packet);
        return everything;
    }

    private void appendPacket(TimestampedPacket packet) {
        if (packet.isSilence) {
            putSilence();
            continuousSilences++;
            lastSilenceTimestamp = packet.timestamp;
        } else {
            interpolateUnrecordedSilence(packet.timestamp);
            put(packet.data);
            continuousSilences = 0;
        }
    }

    private void interpolateUnrecordedSilence(long timestamp) {
        if (continuousSilences > 5) {
            int missedSilences = countSilencesInInterval(lastSilenceTimestamp, timestamp);
            for (int i = 0; i < missedSilences; i++)
                putSilence();
        }
    }

    private void putSilence() {
        put(SILENCE_DATA);
    }

    private void put(byte[] data) {
        System.arraycopy(data, 0, everything, ptr, PACKET_SIZE);
        ptr += PACKET_SIZE;
    }

}
