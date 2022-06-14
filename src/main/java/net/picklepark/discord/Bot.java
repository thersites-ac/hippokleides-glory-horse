package net.picklepark.discord;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.picklepark.discord.adaptor.MessageReceivedActions;
import net.picklepark.discord.adaptor.UserJoinedVoiceActions;
import net.picklepark.discord.adaptor.impl.JdaMessageReceivedActions;
import net.picklepark.discord.adaptor.impl.JdaUserJoinedVoiceActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.DiscordCommandRegistry;
import net.picklepark.discord.command.audio.*;
import net.picklepark.discord.audio.AudioContext;
import net.picklepark.discord.audio.GuildPlayer;
import net.picklepark.discord.command.general.HelpCommand;
import net.picklepark.discord.command.general.MakeAdminCommand;
import net.picklepark.discord.command.general.UnadminCommand;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Bot extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private static final List<Class<? extends DiscordCommand>> COMMANDS = Arrays.asList(
            HelpCommand.class,
            FeatCommand.class,
            SpellCommand.class,
            ChangeVolumeAudioCommand.class,
            DisconnectCommand.class,
            GetVolumeAudioCommand.class,
            LouderAudioCommand.class,
            PauseAudioCommand.class,
            QueueAudioCommand.class,
            RecordCommand.class,
            SkipAudioCommand.class,
            SofterAudioCommand.class,
            UnpauseAudioCommand.class,
            WriteAudioCommand.class,
            RamRanchCommand.class,
            SyncClipsCommand.class,
            DeleteClipCommand.class,
            ListCommandsCommand.class,
            MakeAdminCommand.class,
            UnadminCommand.class,
            StopRecordingCommand.class,
            RandomClipCommand.class,
            RepeatClipCommand.class,
            NukeQueueCommand.class,
            WelcomeCommand.class
    );

    private final Injector injector;
    private final Map<Long, GuildPlayer> guildPlayers;
    private final AudioPlayerManager playerManager;
    private final SqsPollingWorker worker;
 
    public static void main(String[] args) throws Exception {
        JDABuilder.create(System.getProperty("token"), GUILD_MESSAGES, GUILD_VOICE_STATES, GUILD_MEMBERS)
                .addEventListeners(new Bot())
                .build();
    }

    private final DiscordCommandRegistry registry;

    private Bot() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        guildPlayers = new HashMap<>();

        injector = Guice.createInjector(new DefaultModule());
        registry = injector.getInstance(DiscordCommandRegistry.class);
        register(COMMANDS);

        worker = injector.getInstance(SqsPollingWorker.class);
        worker.start();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        MessageReceivedActions actions = buildMessageRecievedActions(event);
        try {
            registry.execute(actions);
        } catch (Exception ex) {
            actions.send("Oh no, I'm dying!");
            logger.error("Error processing guild message", ex);
        }
        super.onGuildMessageReceived(event);
    }

    // fixme: what if the user joins a different audio channel from the one the bot is in?
    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        super.onGuildVoiceJoin(event);
        String user = event.getMember().getUser().getAsTag();
        String channel = event.getChannelJoined().getGuild().getName();
        logger.info(String.format("%s joined %s",
                user,
                channel));
        try {
            registry.welcome(buildUserJoinedVoiceActions(event));
        } catch (NotEnoughQueueCapacityException ex) {
            logger.error(String.format("Could not welcome %s to %s", user, channel), ex);
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

    private void register(List<Class<? extends DiscordCommand>> commands) {
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
