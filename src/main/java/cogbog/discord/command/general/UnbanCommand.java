package cogbog.discord.command.general;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.exception.AuthLevelConflictException;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.exception.UserIdentificationException;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.service.AuthManager;
import com.google.inject.Inject;

import java.io.IOException;

import static java.lang.String.format;

public class UnbanCommand implements DiscordCommand {
    private static final String USER = "user";
    private static final String DSL = format("unban <%s>", USER);
    private static final String HELP_MESSAGE = "Unban a banned user";
    private static final String USER_ALREADY_UNBANNED = "%s was never banned to begin with";

    private final AuthManager authManager;

    @Inject
    public UnbanCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String guildId = actions.getGuildId();
        String user = actions.getArgument(USER);
        try {
            long userId = actions.lookupUserId(user);
            String tag = actions.lookupUserTag(user);
            authManager.unban(guildId, userId);
            actions.send(format("You're back in my good graces, %s", tag));
        } catch (UserIdentificationException e) {
            actions.send(format("Wish I could, but I have no idea who %s is", user));
        } catch (IOException e) {
            throw new DiscordCommandException(e);
        } catch (AuthLevelConflictException e) {
            actions.send(format(USER_ALREADY_UNBANNED, user));
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
