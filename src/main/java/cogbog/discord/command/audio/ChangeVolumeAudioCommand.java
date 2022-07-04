package cogbog.discord.command.audio;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;

public class ChangeVolumeAudioCommand implements DiscordCommand {

    private static final String INPUT_STRING = "volume <volume>";

    @Override
    public void execute(MessageReceivedActions actions) {
        try {
            int volume = Integer.parseInt(actions.getArgument("volume"));
            setValidVolume(actions, volume);
        } catch (NumberFormatException ex) {
            actions.send("Do you know what integers are? Really, " + actions.getArgument("volume") + "?");
        }
    }

    private void setValidVolume(MessageReceivedActions actions, int volume) {
        if (! isValid(volume)) {
            actions.send("Valid volumes are 0-1000. Don't ask me what's wrong with 1001, it's just bad.");
        } else {
            actions.setVolume(volume);
            actions.send("Changed!");
        }
    }

    private boolean isValid(int volume) {
        return 0 <= volume && volume <= 1000;
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return "volume <n>";
    }

    @Override
    public String helpMessage() {
        return "Set the volume.";
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }

}
