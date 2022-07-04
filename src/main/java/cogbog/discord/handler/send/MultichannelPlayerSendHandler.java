package cogbog.discord.handler.send;

import cogbog.discord.exception.UnimplementedException;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class MultichannelPlayerSendHandler implements AudioSendHandler {

    @Override
    public boolean canProvide() {
        throw new UnimplementedException();
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        throw new UnimplementedException();
    }
}

// how to read from the totally-fucked stream that lavaplayer provides
//    public static void main(String[] args) throws DiscordCommandException, IOException, InterruptedException {
//        AudioPlayerManager junk = new DefaultAudioPlayerManager();
//        AudioSourceManagers.registerLocalSource(junk);
//        AudioPlayer player = junk.createPlayer();
//        junk.loadItem("recordings/mixed.wav", new AudioLoadResultHandler() {
//            @Override
//            public void trackLoaded(AudioTrack track) {
//                player.playTrack(track);
//            }
//            @Override
//            public void playlistLoaded(AudioPlaylist playlist) {
//                player.playTrack(playlist.getSelectedTrack());
//            }
//            @Override
//            public void noMatches() {
//                System.out.println("No match");
//            }
//            @Override
//            public void loadFailed(FriendlyException exception) {
//                System.out.println("Load failed");
//            }
//        });
//        Thread.sleep(3000);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        AudioChunkDecoder decoder = DISCORD_OPUS.createDecoder();
//        for (AudioFrame frame = player.provide(); frame != null; frame = player.provide()) {
//            ShortBuffer shortBuffer = ByteBuffer.allocateDirect(DISCORD_OPUS.totalSampleCount() * 2)
//                    .order(ByteOrder.nativeOrder())
//                    .asShortBuffer();
//            decoder.decode(frame.getData(), shortBuffer);
//            outputStream.write(bytesOf(shortBuffer));
//            System.out.println("Wrote a frame");
//        }
//        writeAudioData(outputStream.toByteArray());
//        System.out.println("Done");
//    }
//
//    private static byte[] bytesOf(ShortBuffer shortBuffer) {
//        ByteBuffer byteBuffer = ByteBuffer.allocate(DISCORD_OPUS.totalSampleCount() * 2);
//        while (byteBuffer.position() < byteBuffer.capacity()) {
//            byteBuffer.putShort(shortBuffer.get());
//        }
//        return byteBuffer.array();
//    }
//
//    private static void addAllTo(byte[] source, ArrayList<Byte> dest) {
//        for (byte b: source)
//            dest.add(b);
//    }
//
//    public static void writeAudioData(byte[] data) throws DiscordCommandException {
//        try (AudioInputStream audioInputStream = new AudioInputStream(
//                new ByteArrayInputStream(data),
//                AudioReceiveHandler.OUTPUT_FORMAT,
//                data.length)) {
//            String filename = "recordings/mixed-2.wav";
//            File output = new File(filename);
//            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output);
//        } catch (IOException e) {
//            throw new DiscordCommandException(e);
//        }
//    }

// how to mix using Java's relatively civilized built-in audio API:
//class scratch {
//    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
//        File file1 = new File("recordings/Mon_Oct_25_19-56-18_EDT_2021-cogbog.wav");
//        File file2 = new File("recordings/Thu_Nov_04_21-10-07_EDT_2021-no_this_is_marc.wav");
//        String output = "recordings/mixed.wav";
//        File outputFile = new File(output);
//        AudioInputStream file1stream = AudioSystem.getAudioInputStream(file1);
//        AudioInputStream file2stream = AudioSystem.getAudioInputStream(file2);
//        byte[] mixedData = mix(file1stream, file2stream);
////        byte[] mixedData = file1stream.readAllBytes();
//
//        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(mixedData), file1stream.getFormat(), mixedData.length),
//                WAVE, outputFile);
//    }
//
//    private static byte[] mix(AudioInputStream file1stream, AudioInputStream file2stream) throws IOException {
//        byte[] file1data = file1stream.readAllBytes();
//        byte[] file2data = file2stream.readAllBytes();
//        int size = Math.max(file1data.length, file2data.length);
//        byte[] sum = new byte[size];
//        for (int i = 0; i < size; i++)
//            sum[i] = sum(file1data, file2data, i);
//        return sum;
//    }
//
//    private static byte sum(byte[] file1data, byte[] file2data, int i) {
//        if (i >= file1data.length)
//            return file2data[i];
//        else if (i >= file2data.length)
//            return file1data[i];
//        else
//            return (byte) (file1data[i] + file2data[i]);
//    }
//
//}
