package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.constants.HelpMessages;
import net.picklepark.discord.constants.Messages;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.UserIdentificationException;
import net.picklepark.discord.service.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class MakeAdminCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(MakeAdminCommand.class);
    private static final String INPUT_STRING = "admin <username>";

    private final AuthManager authManager;

    @Inject
    public MakeAdminCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void execute(MessageReceivedActions actions) throws DiscordCommandException {
        String guildName = actions.getGuildId();
        String username = actions.getArgument("username");
        try {
            long userId = actions.lookupUserId(username);
            authManager.addAdmin(guildName, userId);
            actions.send("Welcome to the inner circle, " + username + ".");
        } catch (UserIdentificationException e) {
            logger.warn("Could not find user " + username + " in channel " + guildName);
            actions.send("I can't find a user named " + username);
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
