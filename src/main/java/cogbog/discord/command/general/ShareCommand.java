package cogbog.discord.command.general;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.model.AuthLevel;

public class ShareCommand implements DiscordCommand {
    public static final String SHARE_URL = "https://discord.com/api/oauth2/authorize?client_id=996373324072493056&permissions=3165184&scope=bot";
    private static final String SHARE = "share";

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        actions.send("Click here: " + SHARE_URL);
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.USER;
    }

    @Override
    public String example() {
        return SHARE;
    }

    @Override
    public String helpMessage() {
        return "Get a link to add me to a server you run";
    }

    @Override
    public String userInput() {
        return SHARE;
    }
}
