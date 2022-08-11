package tools;

import cogbog.discord.exception.InvalidAudioPacketException;
import cogbog.discord.exception.NotRecordingException;
import cogbog.discord.service.RecordingService;
import net.dv8tion.jda.api.audio.UserAudio;

public class MockRecordingService implements RecordingService {
    @Override
    public void beginRecording(String guild) {}

    @Override
    public byte[] getUser(String guild, long user) throws NotRecordingException {
        return new byte[0];
    }

    @Override
    public void receive(String guild, UserAudio userAudio) throws NotRecordingException, InvalidAudioPacketException {}

    @Override
    public void stopRecording(String guild) {}
}