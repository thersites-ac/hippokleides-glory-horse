package net.picklepark.discord.command;

import net.dv8tion.jda.api.entities.User;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.constants.Messages;
import net.picklepark.discord.exception.AuthLevelConflictException;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.UserIdentificationException;
import net.picklepark.discord.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class UnadminCommand implements DiscordCommand {
    private static final Logger logger = LoggerFactory.getLogger(UnadminCommand.class);

    private final AuthService authService;

    @Inject
    public UnadminCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        String username = actions.getArgument("user");
        try {
            User user = actions.lookupUser(username);
            authService.demote(user.getIdLong(), actions);
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
        return "unadmin (?<user>.+)";
    }
}
