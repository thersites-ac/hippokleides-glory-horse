package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.general.NoopCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.DynamicCommandManager;
import net.picklepark.discord.service.RemoteStorageService;
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

    private static final DiscordCommand NOOP = new NoopCommand();

    private final RemoteStorageService remoteStorageService;
    private final DynamicCommandManager commandManager;
    private char prefix;
    private final Map<String, DiscordCommand> handlers;

    @Inject
    public DiscordCommandRegistry(RemoteStorageService remoteStorageService, DynamicCommandManager commandManager) {
        handlers = new ConcurrentHashMap<>();
        this.remoteStorageService = remoteStorageService;
        this.commandManager = commandManager;
    }

    private Optional<DiscordCommand> getDynamic(String s) {
        DiscordCommand command = commandManager.lookup(s);
        return Optional.ofNullable(command);
    }

    public void execute(DiscordActions actions) {
        var message = actions.userInput();
        if (hasPrefix(message)) {
            String tail = message.substring(1);
            // FIXME: use the tail here
            DiscordCommand command = lookupAction(tail);
            actions.initMatches(command.getClass().getAnnotation(UserInput.class).value(), tail);
            executeInContext(command, actions);
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
        if (command.getClass().isAnnotationPresent(UserInput.class)) {
            handlers.put(
                    command.getClass().getAnnotation(UserInput.class).value(),
                    command);
        }
    }

    public void register(DiscordCommand... commands) {
        for (var command : commands)
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