package cogbog.discord.command;

import cogbog.discord.command.general.IdkCommand;
import cogbog.discord.model.LocalClip;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.adaptor.UserJoinedVoiceActions;
import cogbog.discord.model.AuthLevel;
import cogbog.discord.exception.DiscordCommandException;
import cogbog.discord.exception.NotEnoughQueueCapacityException;
import cogbog.discord.exception.UnimplementedException;
import cogbog.discord.parse.CommandDsl;
import cogbog.discord.service.AuthManager;
import cogbog.discord.service.ClipManager;
import cogbog.discord.service.WelcomeManager;
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
    private static final String COMMAND_DURATION_MESSAGE = "Executed %s in %s milliseconds";
    private static final String WELCOME_DURATION_MESSAGE = "Welcomed %s to %s in %s milliseconds";

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
            long duration = -System.currentTimeMillis();
            String tail = message.substring(1);
            DiscordCommand command = lookupAction(actions.getGuildId(), tail);
            executeAuthorized(command, actions, tail);
            duration += System.currentTimeMillis();
            logger.info(format(COMMAND_DURATION_MESSAGE, message, duration));
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
        long duration = -System.currentTimeMillis();
        long userId = actions.userId();
        String user = actions.username();
        String guildName = actions.guildName();
        String guildId = actions.guildId();
        LocalClip welcome = welcomeManager.welcome(userId, guildId);
        if (welcome != null && actions.isConnected()) {
            actions.play(welcome.getPath());
            logger.info(format("Welcomed %s (%s) to %s (%s) with %s", user, userId, guildName, guildId, welcome));
        }
        duration += System.currentTimeMillis();
        logger.info(format(WELCOME_DURATION_MESSAGE, user, actions.guildName(), duration));
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

    // fixme: this should be injected, not set manually, unless I intend to let channel owners change the prefix
    // even then this isn't the right approach
    public void prefix(char prefix) {
        this.prefix = prefix;
    }

    private boolean hasPrefix(String message) {
        return message.length() > 0 && message.charAt(0) == prefix;
    }
}