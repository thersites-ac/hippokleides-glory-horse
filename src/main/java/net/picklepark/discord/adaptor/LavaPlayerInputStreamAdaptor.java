package net.picklepark.discord.adaptor;

import com.sedmelluq.discord.lavaplayer.format.transcoder.AudioChunkDecoder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.DISCORD_OPUS;

public class LavaPlayerInputStreamAdaptor extends InputStream {

    private final AudioChunkDecoder decoder;
    private final AudioPlayer player;

    private byte[] data;
    private int cursor;

    public LavaPlayerInputStreamAdaptor(AudioPlayer player) {
        decoder = DISCORD_OPUS.createDecoder();
        this.player = player;
        data = new byte[0];
    }

    @Override
    public int read() throws IOException {
        if (cursor == data.length) {
            AudioFrame frame = player.provide();
            if (frame == null)
                return -1;
            data = decodeData(frame);
            cursor = 0;
        }
        byte b = data[cursor];
        cursor = cursor + 1;
        return b & 0xff;
    }

    private byte[] decodeData(AudioFrame frame) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ShortBuffer shortBuffer = ByteBuffer.allocateDirect(DISCORD_OPUS.totalSampleCount() * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        decoder.decode(frame.getData(), shortBuffer);
        out.write(bytesOf(shortBuffer));
        return out.toByteArray();
    }

    private static byte[] bytesOf(ShortBuffer shortBuffer) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DISCORD_OPUS.totalSampleCount() * 2);
        while (byteBuffer.position() < byteBuffer.capacity()) {
            byteBuffer.putShort(shortBuffer.get());
        }
        return byteBuffer.array();
    }

}
