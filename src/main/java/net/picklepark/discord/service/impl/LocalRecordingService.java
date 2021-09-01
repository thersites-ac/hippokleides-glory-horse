package net.picklepark.discord.service.impl;

import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.command.audio.util.DiscontinuousAudioArray;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.RecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRecordingService implements RecordingService {

    public static final int PACKETS_PER_SECOND = 50;
    public static final int SECONDS_TO_STORE = 60;
    public static final int MAX_STORED_PACKETS = PACKETS_PER_SECOND * SECONDS_TO_STORE;

    private LinkedList<byte[]> combined;
    private ConcurrentHashMap<Long, DiscontinuousAudioArray> userRecordings;

    private boolean recording = false;
    private final Logger logger = LoggerFactory.getLogger(LocalRecordingService.class);

    @Override
    public void beginRecording() {
        recording = true;
        combined = new LinkedList<>();
        userRecordings = new ConcurrentHashMap<>();
    }

    @Override
    public byte[] getUser(User user) throws NotRecordingException {
        if (!recording)
            throw new NotRecordingException();
        long id = user.getIdLong();
        if (userRecordings.containsKey(id))
            return userRecordings.get(id).retrieve();
        else
            return new byte[0];
    }

    @Override
    public void receive(UserAudio userAudio) throws NotRecordingException {
        if (!recording)
            throw new NotRecordingException();
        long id = userAudio.getUser().getIdLong();
        userRecordings.computeIfAbsent(id, meh -> new DiscontinuousAudioArray());
        userRecordings.get(id).store(userAudio.getAudioData(1));
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
    public void receive(CombinedAudio audio) throws NotRecordingException {
        if (!recording)
            throw new NotRecordingException();
        byte[] audioData = audio.getAudioData(1);
        combined.addLast(audioData);
        if (combined.size() > MAX_STORED_PACKETS)
            combined.removeFirst();
    }

}
