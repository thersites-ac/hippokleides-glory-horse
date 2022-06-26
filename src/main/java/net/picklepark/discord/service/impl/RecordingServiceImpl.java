package net.picklepark.discord.service.impl;

import net.dv8tion.jda.api.audio.UserAudio;
import net.picklepark.discord.audio.DiscontinuousAudioArray;
import net.picklepark.discord.exception.InvalidAudioPacketException;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.RecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

@Singleton
public class RecordingServiceImpl implements RecordingService {

    public static final String START_RECORDING_MESSAGE = "Started recording in %s";
    public static final String STOP_RECORDING_MESSAGE = "Stopped recording in %s";

    public final int clipDuration;
    private final Map<String, Map<Long, DiscontinuousAudioArray>> recordings;
    private final Logger logger = LoggerFactory.getLogger(RecordingServiceImpl.class);

    @Inject
    public RecordingServiceImpl(@Named("recording.clip.duration") int clipDuration) {
        this.clipDuration = clipDuration;
        recordings = new ConcurrentHashMap<>();
    }

    @Override
    public void beginRecording(String guild) {
        recordings.put(guild, new ConcurrentHashMap<>());
        logger.info(format(START_RECORDING_MESSAGE, guild));
    }

    @Override
    public byte[] getUser(String guild, long user) throws NotRecordingException {
        var guildRecordings = lookupGuild(guild);
        if (guildRecordings.containsKey(user))
            return guildRecordings.get(user).retrieve();
        else
            return new byte[0];
    }

    private Map<Long, DiscontinuousAudioArray> lookupGuild(String guild) throws NotRecordingException {
        var result = recordings.get(guild);
        if (result == null)
            throw new NotRecordingException(guild);
        else
            return result;
    }

    // fixme: unwrap UserAudio in the handler that talks to this
    @Override
    public void receive(String guild, UserAudio userAudio) throws NotRecordingException, InvalidAudioPacketException {
        var guildRecordings = lookupGuild(guild);
        long id = userAudio.getUser().getIdLong();
        guildRecordings.computeIfAbsent(id, meh -> new DiscontinuousAudioArray(clipDuration));
        guildRecordings.get(id).store(userAudio.getAudioData(1));
    }

    @Override
    public void stopRecording(String guild) {
        recordings.remove(guild);
        logger.info(format(STOP_RECORDING_MESSAGE, guild));
    }

}
