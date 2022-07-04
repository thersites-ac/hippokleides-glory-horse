package cogbog.discord.adaptor;

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

    public LavaPlayerInputStreamAdaptor(AudioPlayer player) throws IOException {
        decoder = DISCORD_OPUS.createDecoder();
        this.player = player;
        nextFrame();
        data = new byte[0];
        cursor = 0;
    }

    @Override
    public int read() throws IOException {
        if (endOfFrame())
            try {
                nextFrame();
            } catch (NullPointerException ex) {
                return -1;
            }
        return readFromFrame();
    }

    private boolean endOfFrame() {
        return cursor == data.length;
    }

    private void nextFrame() throws IOException {
        AudioFrame frame = player.provide();
        decodeData(frame);
    }

    private int readFromFrame() {
        byte b = data[cursor];
        cursor = cursor + 1;
        return b & 0xff;
    }

    private void decodeData(AudioFrame frame) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ShortBuffer shortBuffer = ByteBuffer.allocateDirect(DISCORD_OPUS.totalSampleCount() * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        decoder.decode(frame.getData(), shortBuffer);
        out.write(bytesOf(shortBuffer));
        data = out.toByteArray();
    }

    private static byte[] bytesOf(ShortBuffer shortBuffer) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DISCORD_OPUS.totalSampleCount() * 2);
        while (byteBuffer.position() < byteBuffer.capacity()) {
            byteBuffer.putShort(shortBuffer.get());
        }
        return byteBuffer.array();
    }

}
