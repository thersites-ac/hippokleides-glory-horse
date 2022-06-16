package net.picklepark.discord.command.general;

import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.constants.Messages;
import net.picklepark.discord.exception.AuthLevelConflictException;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.UserIdentificationException;
import net.picklepark.discord.service.AuthManager;
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
        String username = actions.getArgument("user");
        try {
            long user = actions.lookupUserId(username);
            authManager.demote(user, actions);
            actions.send("You're fired, " + username);
        } catch (UserIdentificationException e) {
            actions.send("I don't know who " + username + " is.");
            logger.warn("Ambiguous user for input " + username, e);
        } catch (AuthLevelConflictException e) {
            // FIXME: there's a corner case here where the channel owner tries to unadmin himself and gets
            // this nonsense message
            actions.send(username + " is already beneath my notice.");
            logger.warn("Attempted to demote non-admin user: " + username, e);
        } catch (IOException e) {
            logger.error("While removing admin privileges for " + username, e);
            actions.send(Messages.CANNOT_PERSIST_AUTH_STATE);
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
