package net.picklepark.discord.service;

import net.dv8tion.jda.api.audio.CombinedAudio;
import net.picklepark.discord.exception.NotRecordingException;

public interface RecordingService {
    public void beginRecording();
    public byte[] getCombined() throws NotRecordingException;
    public void receive(CombinedAudio combinedAudio) throws NotRecordingException;
}
