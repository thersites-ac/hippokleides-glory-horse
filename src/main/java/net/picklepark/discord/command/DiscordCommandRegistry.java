package net.picklepark.discord.command;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.adaptor.impl.JdaDiscordActions;
import net.picklepark.discord.annotation.Catches;
import net.picklepark.discord.annotation.SuccessMessage;
import net.picklepark.discord.annotation.UserInput;
import net.picklepark.discord.command.audio.*;
import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.command.audio.util.GuildPlayer;
import net.picklepark.discord.command.general.HelpCommand;
import net.picklepark.discord.command.general.NoopCommand;
import net.picklepark.discord.command.pathfinder.FeatCommand;
import net.picklepark.discord.command.pathfinder.SpellCommand;
import net.picklepark.discord.service.impl.LegacyPrdEmbedder;
import net.picklepark.discord.service.impl.FeatRenderer;
import net.picklepark.discord.service.impl.SpellRenderer;
import net.picklepark.discord.service.impl.DefaultElementScraper;
import net.picklepark.discord.service.impl.DefaultFeatTransformer;
import net.picklepark.discord.service.impl.DefaultSpellTransformer;
import net.picklepark.discord.exception.NoSuchUserException;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.impl.AwsStorageService;
import net.picklepark.discord.service.impl.LocalRecordingService;
import net.picklepark.discord.service.impl.SqsPollingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordCommandRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DiscordCommandRegistry.class);
    private static final String RAM_RANCH_URL = "https://www.youtube.com/watch?v=MADvxFXWvwE";
    private static final LegacyPrdEmbedder legacyPrdEmbedder = new LegacyPrdEmbedder(
                    new DefaultElementScraper(),
                    new FeatRenderer(),
                    new DefaultFeatTransformer(),
                    new SpellRenderer(),
                    new DefaultSpellTransformer());

    private static final DiscordCommand NOOP = new NoopCommand();

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildPlayer> guildPlayers;
    private final List<String> authorizedUsers;
    private final RecordingService recordingService;
    private final PollingService pollingService;
    private final StorageService storageService;
    private char prefix;
    private Map<String, DiscordCommand> handlers;

    public DiscordCommandRegistry() {
        handlers = new ConcurrentHashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        guildPlayers = new HashMap<>();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        authorizedUsers = Arrays.asList("pvhagg#7133", "pvhagg#1387");
        storageService = new AwsStorageService();
        recordingService = new LocalRecordingService();
        pollingService = new SqsPollingService(storageService);
    }

    public DiscordCommand buildAuthorizedCommand(GuildMessageReceivedEvent event) throws NoSuchUserException {
        if (isAuthorized(event))
            return buildCommand(event);
        else
            return NOOP;
    }

    private boolean isAuthorized(GuildMessageReceivedEvent event) {
        return true;
//        return authorizedUsers.contains(event.getAuthor().getAsTag());
    }

    private DiscordCommand buildCommand(GuildMessageReceivedEvent event) throws NoSuchUserException {

        String rawCommand = event.getMessage().getContentRaw();
        String[] command = rawCommand.split(" ");
        AudioContext context = getContext(event);

        if ("~queue".equals(command[0]) && command.length == 2) {
            return new QueueAudioCommand(command[1], context);
        } else if ("~skip".equals(command[0])) {
            return new SkipAudioCommand(context);
        } else if ("~volume".equals(command[0]) && command.length == 1) {
            return new GetVolumeAudioCommand(context);
        } else if ("~volume".equals(command[0]) && command.length == 2) {
            return new ChangeVolumeAudioCommand(command[1], context);
        } else if ("~louder".equals(command[0])) {
            return new LouderAudioCommand(context);
        } else if ("~softer".equals(command[0])) {
            return new SofterAudioCommand(context);
        } else if ("~pause".equals(command[0])) {
            return new PauseAudioCommand(context);
        } else if ("~unpause".equals(command[0])) {
            return new UnpauseAudioCommand(context);
        } else if ("~ramranch".equals(command[0])) {
            return new QueueAudioCommand(RAM_RANCH_URL, context);
        } else if ("~gtfo".equals(command[0])) {
            return new DisconnectCommand(context);
        } else if ("~feat".equals(command[0])) {
            return new FeatCommand(legacyPrdEmbedder);
        } else if ("~spell".equals(command[0])) {
            return new SpellCommand(legacyPrdEmbedder);
        } else if ("~help".equals(command[0])) {
            return new HelpCommand();
        } else if ("~record".equals(command[0])) {
            return new RecordCommand(event, recordingService);
        } else if ("~clip".equals(command[0])) {
            return new WriteAudioCommand(recordingService, argOf(command), storageService, pollingService);
        } else if ('~' == rawCommand.charAt(0)) {
            return fetchFromPollingService(rawCommand, context);
        } else {
            return NOOP;
        }
    }

    private DiscordCommand fetchFromPollingService(String rawCommand, AudioContext context) {
        String unprefixedCommand = rawCommand.substring(1);
        DiscordCommand command = pollingService.lookup(unprefixedCommand, context);
        if (command != null) {
            logger.info("Dynamic command");
            return command;
        } else
            return NOOP;
    }

    private String argOf(String[] command) {
        return String.join(" ", Arrays.asList(command).subList(1, command.length));
    }

    private AudioContext getContext(GuildMessageReceivedEvent event) {
        return new AudioContext(event.getChannel(), getGuildPlayer(event.getGuild()), playerManager);
    }

    private GuildPlayer getGuildPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildPlayer guildPlayer = guildPlayers.get(guildId);
        if (guildPlayer == null) {
            guildPlayer = new GuildPlayer(playerManager);
            guildPlayers.put(guildId, guildPlayer);
        }
        return guildPlayer;
    }

    public void execute(GuildMessageReceivedEvent event) throws Exception {
        DiscordCommand command = buildAuthorizedCommand(event);
        JdaDiscordActions actions = new JdaDiscordActions(event);
        command.execute(actions);
    }

    public void execute(DiscordActions actions) throws Exception {
        var message = actions.userInput();
        if (hasPrefix(message)) {
            DiscordCommand command = lookupAction(message);
            executeInContext(command, actions);
        }
    }

    private void executeInContext(DiscordCommand command, DiscordActions actions) throws Exception {
        try {
            actions.setPattern(command.getClass().getAnnotation(UserInput.class).value());
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
            logger.error("Inavlid access modifier for " + method.getName(), e);
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
        return handlers.keySet().stream()
                .filter(tail::matches)
                .findFirst()
                .map(s -> handlers.get(s))
                .orElse(NOOP);
    }

    private boolean hasPrefix(String message) {
        return message.charAt(0) == prefix;
    }

    public DiscordCommandRegistry register(DiscordCommand command) {
        if (command.getClass().isAnnotationPresent(UserInput.class)) {
            handlers.put(
                    command.getClass().getAnnotation(UserInput.class).value(),
                    command);
        }
        return this;
    }

    public DiscordCommandRegistry prefix(char prefix) {
        this.prefix = prefix;
        return this;
    }
}
