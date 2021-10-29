package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UserInput("queue (?<uri>.+)")
@Help(name = "queue <url>", message = "Add a track to the playlist.")
public class QueueAudioCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(QueueAudioCommand.class);

    @Override
    public void execute(DiscordActions actions) {
        String uri = actions.getArgument("uri");
        actions.queue(uri);
        actions.send("I CANNOT WAIT TO PLAY THIS SONG");
    }

}
