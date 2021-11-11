package net.picklepark.discord.command.general;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Auth;
import net.picklepark.discord.annotation.Help;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.constants.HelpMessages;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.NoSuchUserException;
import net.picklepark.discord.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@UserInput("admin (?<username>.+)")
@Help(name = "admin <username>", message = HelpMessages.ADMIN)
@Auth(Auth.Level.OWNER)
public class MakeAdminCommand implements DiscordCommand {

    private static final Logger logger = LoggerFactory.getLogger(MakeAdminCommand.class);

    private final AuthService authService;

    @Inject
    public MakeAdminCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(DiscordActions actions) throws DiscordCommandException {
        String guildName = actions.getGuildName();
        String username = actions.getArgument("username");
        try {
            long userId = actions.lookupUser(username).getIdLong();
            authService.addAdmin(guildName, userId);
        } catch (NoSuchUserException e) {
            logger.warn("Could not find user " + username + " in channel " + guildName);
            actions.send("I can't find a user named " + username);
        }
    }
}
