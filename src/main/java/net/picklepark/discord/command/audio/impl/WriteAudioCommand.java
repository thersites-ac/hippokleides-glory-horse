package net.picklepark.discord.command.audio.impl;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.audio.impl.handler.NoopHandler;
import net.picklepark.discord.exception.CannotFindUserException;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.impl.AwsStorageService;
import net.picklepark.discord.service.model.Coordinates;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class WriteAudioCommand implements DiscordCommand {

    private static final String FORMAT = "%s-%s.wav";
    private static final String BASE_URL = "http://pickle-park.s3-website.us-east-2.amazonaws.com";
    private static final Logger logger = LoggerFactory.getLogger(WriteAudioCommand.class);

    private final RecordingService recordingService;
    private final GuildMessageReceivedEvent event;
    private final User user;
    private final StorageService storageService;

    private URL location;
    private String key;

    public WriteAudioCommand(GuildMessageReceivedEvent event, RecordingService recordingService, String user, StorageService storageService) throws CannotFindUserException {
        this.recordingService = recordingService;
        this.event = event;
        this.user = determineUser(user);
        this.storageService = storageService;
    }

    private User determineUser(String user) throws CannotFindUserException {
        List<Member> users = event.getChannel().getGuild().getMembersByNickname(user, true);
        if (users.isEmpty()) {
            event.getChannel().sendMessage("No one is named " + user).queue();
            throw new CannotFindUserException(user);
        } else if (users.size() > 1) {
            event.getChannel().sendMessage("Too many damn users named " + user).queue();
            throw new CannotFindUserException(user);
        } else
            return users.get(0).getUser();
    }

    @Override
    public void execute() throws IOException {
        try {
            event.getGuild().getAudioManager().setReceivingHandler(new NoopHandler());
            byte[] data = recordingService.getUser(user);
//            recordingService.stopRecording();
            writeAudioData(data);
            sendCropLink();
        } catch (NotRecordingException e) {
            event.getChannel().sendMessage("I never even had a chance").queue();
        }
    }

    private void sendCropLink() {
        String uriParam = Base64.getEncoder().encodeToString(location.toString().getBytes());
        String keyParam = Base64.getEncoder().encodeToString(key.getBytes());
        try {
            URI cropLink = new URIBuilder(BASE_URL)
                    .addParameter("uri", uriParam)
                    .addParameter("key", keyParam)
                    .build();
            event.getChannel().sendMessage("OK, now go to " + cropLink.toString() + " to trim it.").queue();
        } catch (URISyntaxException e) {
            event.getChannel().sendMessage("I did something incomprehensible, sorry").queue();
            e.printStackTrace();
        }
    }

    private void writeAudioData(byte[] data) throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        AudioInputStream audioInputStream = new AudioInputStream(in, AudioReceiveHandler.OUTPUT_FORMAT, data.length);
        String filename = "recordings/" + makeName(user);
        File output = new File(filename);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output);
        Coordinates coordinates = storageService.store(output);
        location = coordinates.getUrl();
        key = coordinates.getKey();
    }

    private String makeName(User user) {
        return String.format(FORMAT, new Date().toString(), user.getName())
                .replace(':', '-')
                .replace(' ', '_');
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
