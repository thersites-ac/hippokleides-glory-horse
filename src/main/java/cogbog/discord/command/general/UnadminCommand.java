package cogbog.discord.command.general;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.exception.*;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.constants.Messages;
import cogbog.discord.service.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class UnadminCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(UnadminCommand.class);
    private static final String INPUT_STRING = "unadmin <user>";

    private final AuthManager authManager;

    @Inject
    public UnadminCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String user = actions.getArgument("user");
        try {
            long userId = actions.lookupUserId(user);
            var tag = actions.lookupUserTag(user);
            authManager.demote(userId, actions);
            actions.send("You're fired, " + tag);
        } catch (UserIdentificationException e) {
            actions.send("I don't know who that is.");
            logger.warn("Ambiguous user for input " + user, e);
        } catch (AuthLevelConflictException e) {
            actions.send("Already beneath my notice.");
            logger.warn("Attempted to demote non-admin user: " + user, e);
        } catch (CannotDemoteSelfException e) {
            actions.send("You can't step down from your responsibilities!");
            logger.warn("Attempted to demote self: " + user, e);
        } catch (IOException e) {
            logger.error("While removing admin privileges for " + user, e);
            actions.send(Messages.CANNOT_PERSIST_AUTH_STATE);
        } catch (AuthException e) {
            throw new DiscordCommandException(e);
        }
    }

    @Override
    public AuthLevel requiredAuthLevel() {
        return AuthLevel.OWNER;
    }

    @Override
    public String example() {
        return "unadmin <user>";
    }

    @Override
    public String helpMessage() {
        return "Revoke admin privileges";
    }

    @Override
    public String userInput() {
        return INPUT_STRING;
    }
}
