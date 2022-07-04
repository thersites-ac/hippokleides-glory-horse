package tools;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.DiscordCommandException;

public class SilentCommand extends SpyCommand {
    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        super.execute(actions);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String userInput() {
        return "silent";
    }
}