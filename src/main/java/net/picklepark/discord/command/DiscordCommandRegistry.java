package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.general.NoopCommand;
import net.picklepark.discord.exception.DiscordCommandException;
import net.picklepark.discord.service.DynamicCommandManager;
import net.picklepark.discord.service.RemoteStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordCommandRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DiscordCommandRegistry.class);

    private static final DiscordCommand NOOP = new NoopCommand();

    private final RemoteStorageService remoteStorageService;
    private final DynamicCommandManager commandManager;
    private char prefix;
    private Map<String, DiscordCommand> handlers;

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
            DiscordCommand command = lookupAction(message);
            actions.initMatches(command.getClass().getAnnotation(UserInput.class).value(), tail);
            executeInContext(command, actions);
        }
    }

    private void executeInContext(DiscordCommand command, DiscordActions actions) {
        try {
            command.execute(actions);
            sendSuccess(command, actions);
        } catch (DiscordCommandException e) {
            actions.send("Oh no, I'm broken!");
            logger.error("Error executing discord command", e);
        }
    }

    private void sendSuccess(DiscordCommand command, DiscordActions actions) {
        if (command.getClass().isAnnotationPresent(SuccessMessage.class))
            actions.send(command.getClass().getAnnotation(SuccessMessage.class).value());
    }

    private DiscordCommand lookupAction(String message) {
        var tail = message.substring(1);
        logger.info("looking up {}", tail);
        return handlers.keySet().stream()
                .filter(tail::matches)
                .findFirst()
                .map(s -> handlers.get(s))
                .orElse(getDynamic(tail).orElse(NOOP));

    }

    public void register(DiscordCommand command) {
        if (command.getClass().isAnnotationPresent(UserInput.class)) {
            handlers.put(
                    command.getClass().getAnnotation(UserInput.class).value(),
                    command);
        }
    }

    public void register(DiscordCommand... commands) {
        for (var command: commands)
            register(command);
    }

    public void prefix(char prefix) {
        this.prefix = prefix;
    }

    private boolean hasPrefix(String message) {
        return message.length() > 0 && message.charAt(0) == prefix;
    }

}
