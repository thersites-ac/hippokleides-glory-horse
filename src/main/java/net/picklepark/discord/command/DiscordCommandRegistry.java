package net.picklepark.discord.command;

import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.annotation.Catches;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.general.NoopCommand;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordCommandRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DiscordCommandRegistry.class);

    private static final DiscordCommand NOOP = new NoopCommand();

    private final PollingService pollingService;
    private final StorageService storageService;
    private char prefix;
    private Map<String, DiscordCommand> handlers;

    @Inject
    public DiscordCommandRegistry(StorageService storageService, PollingService pollingService) {
        handlers = new ConcurrentHashMap<>();
        this.storageService = storageService;
        this.pollingService = pollingService;
    }

    private Optional<DiscordCommand> fetchFromPollingService(String s) {
        DiscordCommand command = pollingService.lookup(s);
        return Optional.ofNullable(command);
    }

    public void execute(DiscordActions actions) throws Exception {
        var message = actions.userInput();
        if (hasPrefix(message)) {
            String tail = message.substring(1);
            DiscordCommand command = lookupAction(message);
            actions.initMatches(command.getClass().getAnnotation(UserInput.class).value(), tail);
            executeInContext(command, actions);
        }
    }

    private void executeInContext(DiscordCommand command, DiscordActions actions) throws Exception {
        try {
            command.execute(actions);
            sendSuccess(command, actions);
        } catch (Exception e) {
            Optional<Method> handler = exceptionHandler(command, e);
            handler.ifPresent(m -> {
                attemptInvocation(m, command, actions);
                logger.warn("Handled exception while executing " + command.getClass().getName(), e);
            });
            handler.orElseThrow(() -> e);
        }
    }

    private void attemptInvocation(Method method, DiscordCommand command, DiscordActions actions) {
        try {
            method.invoke(command, actions);
        } catch (IllegalAccessException e) {
            logger.error("Invalid access modifier for " + method.getName(), e);
        } catch (InvocationTargetException e) {
            logger.error("Invalid parameters for " + method.getName(), e);
        }
    }

    private Optional<Method> exceptionHandler(DiscordCommand command, Exception e) {
        return Arrays.stream(command.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Catches.class)
                        && method.getAnnotation(Catches.class).value().equals(e.getClass()))
                .findFirst();
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
                .orElse(fetchFromPollingService(tail).orElse(NOOP));

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
