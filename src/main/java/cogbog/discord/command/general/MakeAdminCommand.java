package cogbog.discord.command.general;

import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.constants.HelpMessages;
import cogbog.discord.constants.Messages;
import cogbog.discord.exception.AlreadyAdminException;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.exception.UserIdentificationException;
import cogbog.discord.service.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

import static java.lang.String.format;

public class MakeAdminCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(MakeAdminCommand.class);
    private static final String INPUT_STRING = "admin <username>";
    private static final String DUPLICATE_ADMIN = "Attempted to appoint %s admin in %s, but they're already an admin";

    private final AuthManager authManager;

    @Inject
    public MakeAdminCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String guildId = actions.getGuildId();
        String username = actions.getArgument("username");
        try {
            long userId = actions.lookupUserId(username);
            authManager.addAdmin(guildId, userId);
            String tag = actions.lookupUserTag(username);
            actions.send("Welcome to the inner circle, " + tag + ".");
        } catch (UserIdentificationException e) {
            logger.warn("Could not find user " + username + " in channel " + guildId);
            actions.send("I can't find a user named " + username);
        } catch (IOException e) {
            logger.error("While removing admin privileges for " + username, e);
            actions.send(Messages.CANNOT_PERSIST_AUTH_STATE);
        } catch (AlreadyAdminException e) {
            logger.warn(format(DUPLICATE_ADMIN, username, actions.getGuildName()));
            actions.send(username + " is already an admin!");
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.OWNER;
    }

    @Override
    public String example() {
        return "admin <username>";
    }

    @Override
    public String helpMessage() {
        return HelpMessages.ADMIN;
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }
}
