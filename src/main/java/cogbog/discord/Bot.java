package cogbog.discord;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.command.audio.*;
import cogbog.discord.service.RecordingService;
import com.google.inject.*;
import com.google.inject.name.Names;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import cogbog.discord.adaptor.MessageReceivedActions;
import cogbog.discord.adaptor.UserJoinedVoiceActions;
import cogbog.discord.adaptor.impl.JdaMessageReceivedActions;
import cogbog.discord.adaptor.impl.JdaUserJoinedVoiceActions;
import cogbog.discord.command.DiscordCommandRegistry;
import cogbog.discord.audio.AudioContext;
import cogbog.discord.audio.GuildPlayer;
import cogbog.discord.config.DefaultModule;
import cogbog.discord.service.RemoteStorageService;
import cogbog.discord.worker.SqsPollingWorker;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.String.format;

@Singleton
public class Bot extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private static JDA jda;
    private static Injector injector;

    private final Map<Long, GuildPlayer> guildPlayers;
    private final AudioPlayerManager playerManager;
    private final ExecutorService executorService;
    private final RecordingService recordingService;

    public static void main(String[] args) {
        try {
            injector = Guice.createInjector(new DefaultModule());
            jda = injector.getInstance(JDA.class);

            if (injector.getInstance(Key.get(Boolean.class, Names.named("sqs.poll.enabled")) )) {
                logger.info("SQS polling is enabled");
                var worker = injector.getInstance(SqsPollingWorker.class);
                worker.start();
            } else {
                logger.info("SQS polling is disabled");
            }

            // todo: is there a cleaner way to schedule this after the connection is ready?
            Thread.sleep(3000);
            var remoteAudio = injector.getInstance(RemoteStorageService.class);
            logger.info("I'm in " + jda.getGuilds().size() + " guilds");
            jda.getGuilds().forEach(g -> {
                logger.info(format("Setting up guild %s (%s)", g.getName(), g.getId()));
                remoteAudio.sync(g.getId());
            });

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (jda != null) {
                    jda.shutdown();
                }
                logger.warn("Shutting down.");
            }));
        } catch (Exception ex) {
            logger.error("Exception at startup", ex);
        }
    }

    private final DiscordCommandRegistry registry;

    @Inject
    private Bot(DiscordCommandRegistry registry,
                AudioPlayerManager playerManager,
                ExecutorService executorService,
                RecordingService recordingService,
                Set<Class<? extends DiscordCommand>> commands) {
        this.playerManager = playerManager;
        this.registry = registry;
        this.recordingService = recordingService;

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        guildPlayers = new HashMap<>();

        register(commands);

        this.executorService = executorService;

        logVersion();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        executorService.submit(() -> {
            MessageReceivedActions actions = buildMessageReceivedActions(event);
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
            var eventChannel = event.getChannelJoined();
            var guild = event.getGuild();
            String guildName = guild.getName();
            String guildId = guild.getId();
            logger.info(format("%s joined voice: %s (%s) / %s (%s)",
                    user,
                    guildName, guildId,
                    eventChannel.getName(), eventChannel.getId()));

            var audioManager = event.getGuild().getAudioManager();
            if (!audioManager.isConnected() && eventChannel.getMembers().size() == 1) {
                audioManager.openAudioConnection(eventChannel);
                recordingService.beginRecording(guildId);
                logger.info(format("Joining %s (%s)", eventChannel.getName(), eventChannel.getId()));
            } else if (eventOccurredInConnectedChannel(audioManager, eventChannel)) {
                try {
                    registry.welcome(buildUserJoinedVoiceActions(event));
                } catch (Exception ex) {
                    logger.error(format("Could not welcome %s to %s", user, guildName), ex);
                }
            }
        });
    }

    // fixme: whatever toggle controls autojoin should affect this too
    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        executorService.submit(() -> {
            super.onGuildVoiceLeave(event);
            var audioManager = event.getGuild().getAudioManager();
            var eventChannel = event.getChannelLeft();
            if (eventOccurredInConnectedChannel(audioManager, eventChannel)) {
                var onlyBotsRemain = eventChannel.getMembers().stream()
                        .map(member -> member.getUser().isBot())
                        .reduce(true, (p, q) -> p && q);
                if (onlyBotsRemain) {
                    audioManager.closeAudioConnection();
                    recordingService.stopRecording(event.getGuild().getId());
                    logger.info(format("Leaving %s (%s)", eventChannel.getName(), eventChannel.getId()));
                }
            }
        });
    }

    private UserJoinedVoiceActions buildUserJoinedVoiceActions(GuildVoiceJoinEvent event) {
        AudioContext context = new AudioContext(event.getGuild(), getGuildPlayer(event.getGuild()), playerManager);
        return new JdaUserJoinedVoiceActions(context, event);
    }

    private MessageReceivedActions buildMessageReceivedActions(GuildMessageReceivedEvent event) {
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

    private void logVersion() {
        var stream = getClass().getResourceAsStream("version.txt");
        if (stream != null) {
            Scanner scanner = new Scanner(stream);
            var next = scanner.next();
            logger.info("Version: " + next);
        } else {
            logger.error("Version unknown: no version.txt found");
        }
    }

    private boolean eventOccurredInConnectedChannel(AudioManager audioManager, VoiceChannel channel) {
        var connectedChannel = audioManager.getConnectedChannel();
        return connectedChannel != null && connectedChannel.getId().equals(channel.getId());
    }
}
