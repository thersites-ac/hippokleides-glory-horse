package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.*;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.model.ClipMetadata;
import cogbog.discord.service.RecordingService;
import cogbog.discord.service.RemoteStorageService;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import cogbog.discord.model.Coordinates;
import cogbog.discord.service.UrlShortener;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

public class WriteAudioCommand implements DiscordCommand {

    private static final String FORMAT = "%s-%s.wav";
    private static final String BASE_URL = "http://pickle-park.s3-website.us-east-2.amazonaws.com";

    private static final Logger logger = LoggerFactory.getLogger(WriteAudioCommand.class);
    private static final String INPUT_STRING = "clip <username>";

    private final RecordingService recordingService;
    private final RemoteStorageService remoteStorageService;
    private final UrlShortener urlShortener;
    private final String commandPrefix;

    @Inject
    public WriteAudioCommand(RecordingService recordingService,
                             RemoteStorageService remoteStorageService,
                             UrlShortener urlShortener,
                             @Named("command.prefix") String commandPrefix) {
        this.recordingService = recordingService;
        this.remoteStorageService = remoteStorageService;
        this.urlShortener = urlShortener;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String username = actions.getArgument("username");
        try {
            long user = actions.lookupUserId(username);
            String guild = actions.getGuildId();
            byte[] data = recordingService.getUser(guild, user);
            ClipMetadata metadata = ClipMetadata.builder()
                    .originatingTextChannel(actions.getOriginatingTextChannelId())
                    .creator(actions.getAuthorId())
                    .recordedUser(actions.lookupUserId(username))
                    .build();
            Coordinates coordinates = writeAudioData(data, guild, metadata);
            sendCropLink(actions, coordinates);
        } catch (NotRecordingException e) {
            actions.send("I'm not very turned on right now :(");
        } catch (NoSuchUserException e) {
            actions.send("I can't find the user " + e.getUser() + "!");
        } catch (AmbiguousUserException e) {
            actions.send("Too many damn users named " + username);
        } catch (UserIdentificationException e) {
            actions.send("I can't identify " + username);
            logger.error("Unexpected UserIdentificationException", e);
        } catch (IOException e) {
            actions.send("I can't give you a clean url for the recording right now");
            logger.error("While shortening clip for " + actions.getArgument("username"), e);
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return "clip <username>";
    }

    @Override
    public String helpMessage() {
        return "Clip a user from the voice channel. Make sure to run ~record first";
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }

    private void sendCropLink(MessageReceivedActions actions, Coordinates coordinates) throws IOException {
        try {
            URI cropLink = new URIBuilder(BASE_URL)
                    .addParameter("uri", coordinates.getUrl().toString())
                    .addParameter("key", coordinates.getKey())
                    .addParameter("prefix", coordinates.getPrefix())
                    .addParameter("guild_prefix", commandPrefix)
                    .build();
            String bitlyLink = urlShortener.shorten(cropLink.toString());
            actions.send("OK, now go to " + bitlyLink+ " to trim it.");
        } catch (URISyntaxException e) {
            actions.send("I did something incomprehensible, sorry");
            e.printStackTrace();
        }
    }

    private Coordinates writeAudioData(byte[] data, String guild, ClipMetadata metadata) throws DiscordCommandException {
        try (AudioInputStream audioInputStream = new AudioInputStream(
                new ByteArrayInputStream(data),
                AudioReceiveHandler.OUTPUT_FORMAT,
                data.length)) {
            String baseName = makeName();
            String directory = "recordings/" + guild;
            String filename = directory + "/" + baseName;
            new File(directory).mkdirs();
            File output = new File(filename);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output);
            return remoteStorageService.store(guild, output, metadata);
        } catch (IOException e) {
            throw new DiscordCommandException(e);
        }
    }

    private String makeName() {
        return String.format(FORMAT, UUID.randomUUID(), new Date().toString())
                .replace(':', '-')
                .replace(' ', '_');
    }

}