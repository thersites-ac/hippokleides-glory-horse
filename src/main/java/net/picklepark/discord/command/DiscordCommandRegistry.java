package net.picklepark.discord.command;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.picklepark.discord.adaptor.DiscordActions;
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

import java.io.IOException;
import java.util.*;

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

    public DiscordCommandRegistry() {
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
            return new FeatCommand(argOf(command), event, legacyPrdEmbedder);
        } else if ("~spell".equals(command[0])) {
            return new SpellCommand(argOf(command), event, legacyPrdEmbedder);
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

    public void execute(GuildMessageReceivedEvent event) throws NoSuchUserException, IOException {
        DiscordActions actions = buildActions(event);
        buildAuthorizedCommand(event).execute(actions);
    }

    private DiscordActions buildActions(GuildMessageReceivedEvent event) {
        return null;
    }
}
