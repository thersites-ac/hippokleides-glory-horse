package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.adaptor.UserJoinedVoiceActions;
import net.picklepark.discord.command.general.IdkCommand;
import net.picklepark.discord.model.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;
import net.picklepark.discord.exception.UnimplementedException;
import net.picklepark.discord.model.LocalClip;
import net.picklepark.discord.parse.CommandDsl;
import net.picklepark.discord.service.AuthManager;
import net.picklepark.discord.service.ClipManager;
import net.picklepark.discord.service.WelcomeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Singleton
public class DiscordCommandRegistry {
    public static final String UNAUTHORIZED_MESSAGE = "Lol no";

    private static final Logger logger = LoggerFactory.getLogger(DiscordCommandRegistry.class);

    private static final DiscordCommand NOOP = new IdkCommand();

    private final ClipManager commandManager;
    private final WelcomeManager welcomeManager;
    private char prefix;
    private final Map<Pattern, DiscordCommand> handlers;
    private final AuthManager authManager;

    @Inject
    public DiscordCommandRegistry(ClipManager clipManager, AuthManager authManager, WelcomeManager welcomeManager) {
        handlers = new ConcurrentHashMap<>();
        this.commandManager = clipManager;
        this.authManager = authManager;
        this.welcomeManager = welcomeManager;
    }

    private Optional<DiscordCommand> getDynamic(String guild, String title) {
        logger.info("Looking up command for " + guild + " and " + title);
        DiscordCommand command = commandManager.lookup(guild, title);
        return Optional.ofNullable(command);
    }

    // fixme - this logic should be in the bot
    public void execute(MessageReceivedActions actions, String message) {
        if (hasPrefix(message)) {
            String tail = message.substring(1);
            DiscordCommand command = lookupAction(actions.getGuildId(), tail);
            executeAuthorized(command, actions, tail);
        }
    }

    private void executeAuthorized(DiscordCommand command, MessageReceivedActions actions, String messageContent) {
        AuthLevel level = command.requiredAuthLevel();
        if (authManager.isActionAuthorized(actions, level)) {
            actions.initMatches(command.userInput(), messageContent);
            executeInContext(command, actions);
        } else {
            actions.send(UNAUTHORIZED_MESSAGE);
            logger.warn(format("User %s (%s) tried to execute %s in guild %s (%s) without authorization",
                    actions.getAuthorUsername(),
                    actions.getAuthorId(),
                    messageContent,
                    actions.getGuildName(),
                    actions.getGuildId()));
        }
    }

    private void executeInContext(DiscordCommand command, MessageReceivedActions actions) {
        try {
            command.execute(actions);
        } catch (DiscordCommandException e) {
            actions.send("Oh no, I'm broken!");
            logger.error("Error executing discord command", e);
        }
    }

    public void welcome(UserJoinedVoiceActions actions) throws NotEnoughQueueCapacityException {
        long user = actions.user();
        String guildName = actions.guildName();
        String guildId = actions.guildId();
        LocalClip welcome = welcomeManager.welcome(actions.user(), guildId);
        if (welcome != null && actions.isConnected()) {
            actions.play(welcome.getPath());
            logger.info(format("Welcomed %s to %s (%s) with %s", user, guildName, guildId, welcome));
        } else {
            logger.error(format("Welcome was null in %s for %s", guildName, user));
        }
    }

    private DiscordCommand lookupAction(String guild, String message) {
        logger.info("looking up {}", message);
        return handlers.keySet().stream()
                .filter(p -> p.matcher(message).matches())
                .findFirst()
                .map(handlers::get)
                .orElse(getDynamic(guild, message).orElse(NOOP));

    }

    public void register(DiscordCommand command) {
        if (command instanceof IdkCommand)
            throw new UnimplementedException();
        else
            handlers.put(new CommandDsl(command.userInput()).toPattern(), command);
    }

    public void register(DiscordCommand... commands) {
        for (DiscordCommand command : commands)
            register(command);
    }

    public Collection<DiscordCommand> getCommands() {
        return handlers.values();
    }

    public void prefix(char prefix) {
        this.prefix = prefix;
    }

    private boolean hasPrefix(String message) {
        return message.length() > 0 && message.charAt(0) == prefix;
    }
}