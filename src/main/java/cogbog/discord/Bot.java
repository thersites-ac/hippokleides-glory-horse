package cogbog.discord;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.command.audio.*;
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
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.adaptor.UserJoinedVoiceActions;
import cogbog.discord.adaptor.impl.JdaMessageReceivedActions;
import cogbog.discord.adaptor.impl.JdaUserJoinedVoiceActions;
import cogbog.discord.command.DiscordCommandRegistry;
import cogbog.discord.audio.AudioContext;
import cogbog.discord.audio.GuildPlayer;
import cogbog.discord.config.DefaultModule;
import cogbog.discord.exception.NotEnoughQueueCapacityException;
import cogbog.discord.service.RemoteStorageService;
import cogbog.discord.worker.SqsPollingWorker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Bot extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private static JDA jda;
    private static Injector injector;

    private final Map<Long, GuildPlayer> guildPlayers;
    private final AudioPlayerManager playerManager;
    private final ExecutorService executorService;

    public static void main(String[] args) throws Exception {
        injector = Guice.createInjector(new DefaultModule());
        var bot = injector.getInstance(Bot.class);
        jda = JDABuilder.create(System.getProperty("token"), GUILD_MESSAGES, GUILD_VOICE_STATES, GUILD_MEMBERS)
                .addEventListeners(bot)
                .build();

        injector.getInstance(SqsPollingWorker.class).start();

        // todo: is there a cleaner way to schedule this after the connection is ready?
        Thread.sleep(3000);
        var remoteAudio = injector.getInstance(RemoteStorageService.class);
        logger.info("I'm in " + jda.getGuilds().size() + " guilds");
        jda.getGuilds().forEach(g -> {
            logger.info(format("Setting up guild %s (%s)", g.getName(), g.getId()));
            remoteAudio.sync(g.getId());
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdown();
            logger.warn("Shutting down.");
        }));
    }

    private final DiscordCommandRegistry registry;

    @Inject
    private Bot(DiscordCommandRegistry registry,
                AudioPlayerManager playerManager,
                ExecutorService executorService,
                Set<Class<? extends DiscordCommand>> commands) {
        this.playerManager = playerManager;
        this.registry = registry;
        registry.prefix('~');

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        guildPlayers = new HashMap<>();

        register(commands);

        this.executorService = executorService;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        executorService.submit(() -> {
            MessageReceivedActions actions = buildMessageRecievedActions(event);
            try {
                registry.execute(actions, event.getMessage().getContentRaw());
            } catch (Exception ex) {
                actions.send("Oh no, I'm dying!");
                logger.error("Error processing guild message", ex);
            }
            super.onGuildMessageReceived(event);
        });
    }

    // fixme: make this a toggleable feature
    // fixme: different event listeners should probably be different classes
    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        executorService.submit(() -> {
            super.onGuildVoiceJoin(event);
            String user = event.getMember().getUser().getAsTag();
            String guild = event.getChannelJoined().getGuild().getName();
            long guildId = event.getChannelJoined().getIdLong();
            logger.info(format("%s joined %s (%s)", user, guild, guildId));
            var voiceChannelIds = jda.getAudioManagers().stream()
                    .map(AudioManager::getConnectedChannel)
                    .filter(Objects::nonNull)
                    .map(ISnowflake::getIdLong)
                    .collect(Collectors.toSet());
            if (voiceChannelIds.contains(guildId)) {
                try {
                    registry.welcome(buildUserJoinedVoiceActions(event));
                } catch (NotEnoughQueueCapacityException ex) {
                    logger.error(format("Could not welcome %s to %s", user, guild), ex);
                }
            }
        });
    }

    private UserJoinedVoiceActions buildUserJoinedVoiceActions(GuildVoiceJoinEvent event) {
        AudioContext context = new AudioContext(event.getGuild(), getGuildPlayer(event.getGuild()), playerManager);
        return new JdaUserJoinedVoiceActions(context, event);
    }

    private MessageReceivedActions buildMessageRecievedActions(GuildMessageReceivedEvent event) {
        AudioContext context = new AudioContext(event.getGuild(), getGuildPlayer(event.getGuild()), playerManager);
        return new JdaMessageReceivedActions(event, context);
    }

    private void register(Collection<Class<? extends DiscordCommand>> commands) {
        boolean collision =
                commands.stream().anyMatch(c -> c.equals(QueueAudioCommand.class)) &&
                        commands.stream().anyMatch(c -> c.equals(PlayClipCommand.class)
                                || c.equals(RandomClipCommand.class)
                                || c.equals(RepeatClipCommand.class));
        // fixme: contention will also occur if any user has a welcome set and other audio is playing
        if (collision) {
            logger.warn("Risk of audio queue contention because both clip and other audio playback is allowed");
        }
        for (Class<? extends DiscordCommand> command: commands) {
            try {
                registry.register(injector.getInstance(command));
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
