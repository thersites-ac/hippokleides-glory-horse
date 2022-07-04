package net.picklepark.discord;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.adaptor.UserJoinedVoiceActions;
import net.picklepark.discord.adaptor.impl.JdaMessageReceivedActions;
import net.picklepark.discord.adaptor.impl.JdaUserJoinedVoiceActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.DiscordCommandRegistry;
import net.picklepark.discord.command.audio.*;
import net.picklepark.discord.audio.AudioContext;
import net.picklepark.discord.audio.GuildPlayer;
import net.picklepark.discord.command.general.*;
import net.picklepark.discord.command.pathfinder.FeatCommand;
import net.picklepark.discord.command.pathfinder.SpellCommand;
import net.picklepark.discord.config.DefaultModule;
import net.picklepark.discord.exception.NotEnoughQueueCapacityException;
import net.picklepark.discord.service.RemoteStorageService;
import net.picklepark.discord.worker.SqsPollingWorker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Bot extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private static final Set<Class<? extends DiscordCommand>> COMMANDS = Set.of(
            BanCommand.class,
            ChangeVolumeAudioCommand.class,
            DeleteClipCommand.class,
            DisconnectCommand.class,
            FeatCommand.class,
            GetVolumeAudioCommand.class,
            HelpCommand.class,
            ListClipsCommand.class,
            LouderAudioCommand.class,
            MakeAdminCommand.class,
            NukeQueueCommand.class,
            PauseAudioCommand.class,
            QueueAudioCommand.class,
            RamRanchCommand.class,
            RandomClipCommand.class,
            RecordCommand.class,
            RepeatClipCommand.class,
            SkipAudioCommand.class,
            SofterAudioCommand.class,
            SpellCommand.class,
            StopRecordingCommand.class,
            SyncClipsCommand.class,
            UnadminCommand.class,
            UnbanCommand.class,
            UnpauseAudioCommand.class,
            WelcomeCommand.class,
            WriteAudioCommand.class
    );
    private static JDA jda;
    private static Injector injector;

    private final Map<Long, GuildPlayer> guildPlayers;
    private final AudioPlayerManager playerManager;

    public static void main(String[] args) throws Exception {
        boolean collision =
                COMMANDS.stream().anyMatch(c -> c.equals(QueueAudioCommand.class)) &&
                COMMANDS.stream().anyMatch(c -> c.equals(PlayClipCommand.class)
                        || c.equals(RandomClipCommand.class)
                        || c.equals(RepeatClipCommand.class));
        if (collision) {
            logger.warn("Risk of audio queue contention because both clip and other audio playback is allowed");
        }
        // fixme: contention will also occur if any user has a welcome set and other audio is playing
        injector = Guice.createInjector(new DefaultModule());
        var bot = injector.getInstance(Bot.class);
        jda = JDABuilder.create(System.getProperty("token"), GUILD_MESSAGES, GUILD_VOICE_STATES, GUILD_MEMBERS)
                .addEventListeners(bot)
                .build();

        // todo: is there a cleaner way to schedule this after the connection is ready?
        Thread.sleep(3000);
        var remoteAudio = injector.getInstance(RemoteStorageService.class);
        logger.info("I'm in " + jda.getGuilds().size() + " guilds");
        jda.getGuilds().forEach(g -> {
            logger.info(String.format("Setting up guild %s (%s)", g.getName(), g.getId()));
            remoteAudio.sync(g.getId());
        });
    }

    private final DiscordCommandRegistry registry;

    @Inject
    private Bot(DiscordCommandRegistry registry, SqsPollingWorker worker, AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
        this.registry = registry;

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        guildPlayers = new HashMap<>();

        register(COMMANDS);

        worker.start();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        MessageReceivedActions actions = buildMessageRecievedActions(event);
        try {
            registry.execute(actions, event.getMessage().getContentRaw());
        } catch (Exception ex) {
            actions.send("Oh no, I'm dying!");
            logger.error("Error processing guild message", ex);
        }
        super.onGuildMessageReceived(event);
    }

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        super.onGuildVoiceJoin(event);
        String user = event.getMember().getUser().getAsTag();
        String guild = event.getChannelJoined().getGuild().getName();
        long guildId = event.getChannelJoined().getIdLong();
        logger.info(String.format("%s joined %s (%s)", user, guild, guildId));
        var voiceChannelIds = jda.getAudioManagers().stream()
                .map(AudioManager::getConnectedChannel)
                .filter(Objects::nonNull)
                .map(ISnowflake::getIdLong)
                .collect(Collectors.toSet());
        if (voiceChannelIds.contains(guildId)) {
            try {
                registry.welcome(buildUserJoinedVoiceActions(event));
            } catch (NotEnoughQueueCapacityException ex) {
                logger.error(String.format("Could not welcome %s to %s", user, guild), ex);
            }
        }
    }

    private UserJoinedVoiceActions buildUserJoinedVoiceActions(GuildVoiceJoinEvent event) {
        AudioContext context = new AudioContext(event.getGuild(), getGuildPlayer(event.getGuild()), playerManager);
        return new JdaUserJoinedVoiceActions(context, event);
    }

    private MessageReceivedActions buildMessageRecievedActions(GuildMessageReceivedEvent event) {
        AudioContext context = new AudioContext(event.getGuild(), getGuildPlayer(event.getGuild()), playerManager);
        return new JdaMessageReceivedActions(event, context);
    }

    private void register(Set<Class<? extends DiscordCommand>> commands) {
        for (Class<? extends DiscordCommand> command: commands) {
            try {
                registry.register(injector.getInstance(command));
                registry.prefix('~');
                logger.info("Registered {}", command.getName());
            } catch (Exception e) {
                logger.error("Exception at startup", e);
                System.exit(1);
            }
        }
    }


    private GuildPlayer getGuildPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        guildPlayers.computeIfAbsent(guildId, id -> new GuildPlayer(playerManager));
        return guildPlayers.get(guildId);
    }
}
