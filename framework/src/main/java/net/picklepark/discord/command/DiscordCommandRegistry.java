package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.constants.AuthLevel;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.exception.UnimplementedException;
import net.picklepark.discord.service.AuthService;
import net.picklepark.discord.service.ClipManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DiscordCommandRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DiscordCommandRegistry.class);

    private static final DiscordCommand NOOP = new IdkCommand();

    private final ClipManager commandManager;
    private char prefix;
    private final Map<String, DiscordCommand> handlers;
    private final AuthService authService;

    @Inject
    public DiscordCommandRegistry(ClipManager commandManager, AuthService authService) {
        handlers = new ConcurrentHashMap<>();
        this.commandManager = commandManager;
        this.authService = authService;
    }

    private Optional<DiscordCommand> getDynamic(String s) {
        DiscordCommand command = commandManager.lookup(s);
        return Optional.ofNullable(command);
    }

    public void execute(DiscordActions actions) {
        String message = actions.userInput();
        if (hasPrefix(message)) {
            String tail = message.substring(1);
            DiscordCommand command = lookupAction(tail);
            executeAuthorized(command, actions, tail);
        }
    }

    private void executeAuthorized(DiscordCommand command, DiscordActions actions, String messageContent) {
        AuthLevel level = command.requiredAuthLevel();
        if (authService.isActionAuthorized(actions, level)) {
            actions.initMatches(command.userInput(), messageContent);
            executeInContext(command, actions);
        } else {
            actions.send("Lol no");
        }
    }

    private void executeInContext(DiscordCommand command, DiscordActions actions) {
        try {
            command.execute(actions);
        } catch (DiscordCommandException e) {
            actions.send("Oh no, I'm broken!");
            logger.error("Error executing discord command", e);
        }
    }

    private DiscordCommand lookupAction(String message) {
        logger.info("looking up {}", message);
        return handlers.keySet().stream()
                .filter(message::matches)
                .findFirst()
                .map(handlers::get)
                .orElse(getDynamic(message).orElse(NOOP));

    }

    public void register(DiscordCommand command) {
        if (command instanceof IdkCommand)
            throw new UnimplementedException();
        else
            handlers.put(command.userInput(), command);
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