package cogbog.discord.command.general;

import cogbog.discord.command.DiscordCommand;
import com.google.inject.Inject;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.exception.UserIdentificationException;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.AuthManager;

import java.io.IOException;

import static java.lang.String.format;

public class BanCommand implements DiscordCommand {
    private static final String USER = "user";
    private static final String DSL = format("ban <%s>", USER);
    private static final String HELP_MESSAGE = "Block someone from interacting with me";
    private static final String UNCLEAR_USER_MESSAGE = "I don't know who %s is";

    private final AuthManager authManager;

    @Inject
    public BanCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String user = actions.getArgument(USER);
        try {
            long userId = actions.lookupUserId(user);
            String guildId = actions.getGuildId();
            authManager.ban(guildId, userId);
            actions.send("You done goofed, " + actions.getArgument("user"));
        } catch (UserIdentificationException e) {
            actions.send(format(UNCLEAR_USER_MESSAGE, user));
        } catch (IOException e) {
            throw new DiscordCommandException(e);
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.ADMIN;
    }

    @Override
    public String example() {
        return DSL;
    }

    @Override
    public String helpMessage() {
        return HELP_MESSAGE;
    }

    @Override
    public String userInput() {
        return DSL;
    }
}
