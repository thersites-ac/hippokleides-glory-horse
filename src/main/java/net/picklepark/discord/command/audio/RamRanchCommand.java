package net.picklepark.discord.command.audio;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;

@UserInput("ramranch")
@Help(name = "ramranch", message = "RAM RANCH REALLY ROCKS")
public class RamRanchCommand implements DiscordCommand {

    private static final String RAM_RANCH_URL = "https://www.youtube.com/watch?v=MADvxFXWvwE";

    @Override
    public void execute(DiscordActions actions) {
        actions.connect();
        actions.queue(RAM_RANCH_URL);
        actions.send("enjoy :)");
    }
}
