package net.picklepark.discord.service.impl;

import net.dv8tion.jda.api.audio.CombinedAudio;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.RecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class LocalRecordingService implements RecordingService {

    private LinkedList<byte[]> combined;
    private boolean recording = false;
    private final Logger logger = LoggerFactory.getLogger(LocalRecordingService.class);

    @Override
    public void beginRecording() {
        recording = true;
        combined = new LinkedList<>();
    }

    @Override
    public byte[] getCombined() throws NotRecordingException {
        if (!recording)
            throw new NotRecordingException();
        int len = 0;
        for (byte[] data: combined)
            len += data.length;
        byte[] all = new byte[len];
        int ptr = 0;
        for (byte[] data: combined) {
            System.arraycopy(data, 0, all, ptr, data.length);
            ptr += data.length;
        }
        return all;
    }

    @Override
    public void receive(CombinedAudio combinedAudio) throws NotRecordingException {
        if (!recording)
            throw new NotRecordingException();
        byte[] audioData = combinedAudio.getAudioData(1);
        combined.addLast(audioData);
    }

}
