package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueAudioCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(QueueAudioCommand.class);

    @Override
    public void execute(DiscordActions actions) {
        String uri = actions.getArgument("uri");
        actions.queueChannelOne(uri);
        actions.send("I CANNOT WAIT TO PLAY THIS SONG");
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "queue <url>";
    }

    @Override
    public String helpMessage() {
        return "Add a track to the playlist.";
    }

    @Override
    public String userInput() {
        return "queue (?<uri>.+)";
    }

}
