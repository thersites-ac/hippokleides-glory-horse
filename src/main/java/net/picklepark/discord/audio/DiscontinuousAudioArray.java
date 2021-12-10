package net.picklepark.discord.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;

import static net.picklepark.discord.constants.AudioConstants.SILENCE_DATA;

public class DiscontinuousAudioArray {

    private final long maxInterval;

    private Logger logger = LoggerFactory.getLogger(DiscontinuousAudioArray.class);

    private final LinkedList<TimestampedPacket> audio;

    public DiscontinuousAudioArray() {
        audio = new LinkedList<>();
        maxInterval = 30000L;
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
        return Arrays.equals(data, SILENCE_DATA);
    }

}
