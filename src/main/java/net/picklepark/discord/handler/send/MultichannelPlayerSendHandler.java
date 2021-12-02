package net.picklepark.discord.handler.send;

import club.minnced.opus.util.OpusLibrary;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import tomp2p.opuswrapper.Opus;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;


public class MultichannelPlayerSendHandler implements AudioSendHandler {

    private AudioPlayerSendHandler channelOne;
    private AudioPlayerSendHandler channelTwo;
    private boolean channelOneCanProvide;
    private boolean channelTwoCanPrivide;

    public MultichannelPlayerSendHandler() {
        channelOneCanProvide = false;
        channelTwoCanPrivide = false;
    }

    @Override
    public boolean canProvide() {
        channelOneCanProvide = channelOne.canProvide();
        channelTwoCanPrivide = channelTwo.canProvide();
        return channelOneCanProvide || channelTwoCanPrivide;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        if (! channelOneCanProvide)
            return channelTwo.provide20MsAudio();
        else if (! channelTwoCanPrivide)
            return channelOne.provide20MsAudio();
        else {
            return mix(channelOne.provide20MsAudio(), channelTwo.provide20MsAudio());
        }
    }

    private ByteBuffer mix(ByteBuffer channelOneAudio, ByteBuffer channelTwoAudio) {
        return null;
    }

    public void setChannelTwo(AudioPlayerSendHandler channelTwo) {
        this.channelTwo = channelTwo;
    }

    public void setChannelOne(AudioPlayerSendHandler channelOne) {
        this.channelOne = channelOne;
    }
}
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
