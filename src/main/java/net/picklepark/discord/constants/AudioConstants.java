package net.picklepark.discord.constants;

import java.util.Arrays;

public class AudioConstants {
    public static final int BYTES_PER_MS = 192;
    public static final int MS_PER_PACKET = 20;
    public static final int PACKET_SIZE = BYTES_PER_MS * MS_PER_PACKET;
    public static final byte[] SILENCE_DATA = new byte[PACKET_SIZE];
    static {
        Arrays.fill(SILENCE_DATA, (byte) 0);
    }

}
