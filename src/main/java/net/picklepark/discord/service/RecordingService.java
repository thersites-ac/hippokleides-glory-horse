package net.picklepark.discord.service;

import net.dv8tion.jda.api.audio.UserAudio;
import net.picklepark.discord.exception.InvalidAudioPacketException;
import net.picklepark.discord.exception.NotRecordingException;

public interface RecordingService {
    void beginRecording(String guild);
    byte[] getUser(String guild, long user) throws NotRecordingException;
    void receive(String guild, UserAudio userAudio) throws NotRecordingException, InvalidAudioPacketException;
    void stopRecording(String guild);
}
