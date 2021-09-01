package net.picklepark.discord.command.audio.impl;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.impl.handler.NoopHandler;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.RecordingService;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class WriteAudioCommand implements DiscordCommand {

    private final RecordingService recordingService;
    private final GuildMessageReceivedEvent event;

    public WriteAudioCommand(GuildMessageReceivedEvent event, RecordingService recordingService) {
        this.recordingService = recordingService;
        this.event = event;
    }

    @Override
    public void execute() throws IOException {
        try {
            event.getGuild().getAudioManager().setReceivingHandler(new NoopHandler());
            byte[] data = recordingService.getUser(event.getAuthor());
            writeAudioData(data);
        } catch (NotRecordingException e) {
            e.printStackTrace();
        }
    }

    private void writeAudioData(byte[] data) throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        AudioInputStream audioInputStream = new AudioInputStream(in, AudioReceiveHandler.OUTPUT_FORMAT, data.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File("demo.wav"));
    }

    public static byte[] flatten(List<byte[]> data) {
        int totalLen = 0;
        for (byte[] bytes: data)
            totalLen += bytes.length;

        byte[] combined = new byte[totalLen];
        int ptr = 0;
        for (byte[] bytes: data) {
            System.arraycopy(bytes, 0, combined, ptr, bytes.length);
            ptr += bytes.length;
        }
        return combined;
    }
}
