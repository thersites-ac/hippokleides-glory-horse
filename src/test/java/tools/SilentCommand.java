package tools;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;

public class SilentCommand extends SpyCommand {
    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        super.execute(actions);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ANY;
    }

    @Override
    public String userInput() {
        return "silent";
    }
}