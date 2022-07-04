package cogbog.discord.service;

import cogbog.discord.exception.InvalidAudioPacketException;
import cogbog.discord.exception.NotRecordingException;
import net.dv8tion.jda.api.audio.UserAudio;

public interface RecordingService {
    void beginRecording(String guild);
    byte[] getUser(String guild, long user) throws NotRecordingException;
    void receive(String guild, UserAudio userAudio) throws NotRecordingException, InvalidAudioPacketException;
    void stopRecording(String guild);
}
