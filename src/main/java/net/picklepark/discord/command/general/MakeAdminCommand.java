package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.constants.HelpMessages;
import net.picklepark.discord.constants.Messages;
import net.picklepark.discord.exception.AlreadyAdminException;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.UserIdentificationException;
import net.picklepark.discord.service.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Locale;

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
            // fixme: this sends the command argument `username`, which is approximate. Send a canonical form instead.
            actions.send("Welcome to the inner circle, " + username + ".");
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
