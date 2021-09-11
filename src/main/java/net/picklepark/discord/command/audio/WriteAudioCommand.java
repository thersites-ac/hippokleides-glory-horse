package net.picklepark.discord.command.audio;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.handler.NoopHandler;
import net.picklepark.discord.exception.NoSuchUserException;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.model.Coordinates;
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
    private final StorageService storageService;
    private final PollingService pollingService;
    private final String username;

    private URL location;
    private String key;

    public WriteAudioCommand(RecordingService recordingService, String username, StorageService storageService, PollingService pollingService) {
        this.username = username;
        this.recordingService = recordingService;
        this.storageService = storageService;
        this.pollingService = pollingService;
    }

    @Override
    public void execute(DiscordActions actions) throws IOException {
        try {
            actions.setReceivingHandler(new NoopHandler());
            var user = actions.lookupUser(username);
            byte[] data = recordingService.getUser(user);
            writeAudioData(data);
            sendCropLink(actions);
        } catch (NotRecordingException | NoSuchUserException e) {
            actions.send("I never even had a chance");
        }
    }

    private void sendCropLink(DiscordActions actions) {
        String uriParam = Base64.getEncoder().encodeToString(location.toString().getBytes());
        String keyParam = Base64.getEncoder().encodeToString(key.getBytes());
        try {
            URI cropLink = new URIBuilder(BASE_URL)
                    .addParameter("uri", uriParam)
                    .addParameter("key", keyParam)
                    .build();
            actions.send("OK, now go to " + cropLink.toString() + " to trim it.");
        } catch (URISyntaxException e) {
            actions.send("I did something incomprehensible, sorry");
            e.printStackTrace();
        }
    }

    private void writeAudioData(byte[] data) throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        AudioInputStream audioInputStream = new AudioInputStream(in, AudioReceiveHandler.OUTPUT_FORMAT, data.length);
        String baseName = makeName(username);
        String filename = "recordings/" + baseName;
        File output = new File(filename);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output);
        Coordinates coordinates = storageService.store(output);
        location = coordinates.getUrl();
        key = coordinates.getKey();
        pollingService.expect(baseName);
    }

    private String makeName(String username) {
        return String.format(FORMAT, new Date().toString(), username)
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
