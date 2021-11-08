package net.picklepark.discord.command.audio;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.NoSuchUserException;
import net.picklepark.discord.exception.NotRecordingException;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.RemoteStorageService;
import net.picklepark.discord.model.Coordinates;
import net.picklepark.discord.service.UrlShortener;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

@UserInput("clip (?<username>.+)")
@Help(name = "clip <username>", message = "Clip a user from the voice channel. Make sure to run ~record first.")
public class WriteAudioCommand implements DiscordCommand {

    private static final String FORMAT = "%s-%s.wav";
    private static final String BASE_URL = "http://pickle-park.s3-website.us-east-2.amazonaws.com";

    private static final Logger logger = LoggerFactory.getLogger(WriteAudioCommand.class);

    private final RecordingService recordingService;
    private final RemoteStorageService remoteStorageService;
    private final UrlShortener urlShortener;

    @Inject
    public WriteAudioCommand(RecordingService recordingService,
                             RemoteStorageService remoteStorageService,
                             UrlShortener urlShortener) {
        this.recordingService = recordingService;
        this.remoteStorageService = remoteStorageService;
        this.urlShortener = urlShortener;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        try {
            String username = actions.getArgument("username");
            User user = actions.lookupUser(username);
            byte[] data = recordingService.getUser(user);
            Coordinates coordinates = writeAudioData(data, username);
            sendCropLink(actions, coordinates.getUrl(), coordinates.getKey());
        } catch (NotRecordingException e) {
            actions.send("I'm not very turned on right now :(");
        } catch (NoSuchUserException e) {
            actions.send("I can't find the user " + e.getUser() + "!");
        } catch (IOException e) {
            actions.send("I can't give you a clean url for the recording right now");
            logger.error("While shortening clip for " + actions.getArgument("username"), e);
        }
    }

    private void sendCropLink(DiscordActions actions, URL location, String key) throws IOException {
        String uriParam = URLEncoder.encode(location.toString(), UTF_8);
        String keyParam = URLEncoder.encode(key, UTF_8);
        try {
            URI cropLink = new URIBuilder(BASE_URL)
                    .addParameter("uri", uriParam)
                    .addParameter("key", keyParam)
                    .build();
            String bitlyLink = urlShortener.shorten(cropLink.toString());
            actions.send("OK, now go to " + bitlyLink+ " to trim it.");
        } catch (URISyntaxException e) {
            actions.send("I did something incomprehensible, sorry");
            e.printStackTrace();
        }
    }

    private Coordinates writeAudioData(byte[] data, String username) throws DiscordCommandException {
        try (AudioInputStream audioInputStream = new AudioInputStream(
                new ByteArrayInputStream(data),
                AudioReceiveHandler.OUTPUT_FORMAT,
                data.length)) {
            String baseName = makeName(username);
            String filename = "recordings/" + baseName;
            File output = new File(filename);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output);
            return remoteStorageService.store(output);
        } catch (IOException e) {
            throw new DiscordCommandException(e);
        }
    }

    private String makeName(String username) {
        return String.format(FORMAT, new Date().toString(), username)
                .replace(':', '-')
                .replace(' ', '_');
    }

}