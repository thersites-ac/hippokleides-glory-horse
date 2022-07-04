package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;

public class GetVolumeAudioCommand implements DiscordCommand {

    @Override
    public void execute(MessageReceivedActions actions) {
        int volume = actions.getVolume();
        actions.send("Current volume is " + volume);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return "volume";
    }

    @Override
    public String helpMessage() {
        return "Get the current volume.";
    }

    @Override
    public String userInput() {
        return "volume";
    }
}
