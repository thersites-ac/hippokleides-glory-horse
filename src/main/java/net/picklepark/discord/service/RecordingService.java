package net.picklepark.discord.service;

import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.exception.NotRecordingException;

public interface RecordingService {
    public void beginRecording();
    public byte[] getCombined() throws NotRecordingException;
    public void receive(CombinedAudio combinedAudio) throws NotRecordingException;
    public byte[] getUser(long user) throws NotRecordingException;
    public void receive(UserAudio userAudio) throws NotRecordingException;
    public boolean isRecording();
    public void stopRecording();
}
