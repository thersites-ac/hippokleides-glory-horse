package net.picklepark.discord;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.picklepark.discord.adaptor.DiscordActions;
import net.picklepark.discord.adaptor.impl.JdaDiscordActions;
import net.picklepark.discord.command.DiscordCommand;
import net.picklepark.discord.command.DiscordCommandRegistry;
import net.picklepark.discord.command.audio.*;
import net.picklepark.discord.command.audio.util.AudioContext;
import net.picklepark.discord.command.audio.util.GuildPlayer;
import net.picklepark.discord.command.general.HelpCommand;
import net.picklepark.discord.command.pathfinder.FeatCommand;
import net.picklepark.discord.command.pathfinder.SpellCommand;
import net.picklepark.discord.config.DefaultModule;
import net.picklepark.discord.worker.SqsPollingWorker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

public class Bot extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
 
    private final Injector injector;
    private final Map<Long, GuildPlayer> guildPlayers;
    private final AudioPlayerManager playerManager;
    private final SqsPollingWorker worker;
 
    public static void main(String[] args) throws Exception {
        JDABuilder.create(System.getProperty("token"), GUILD_MESSAGES, GUILD_VOICE_STATES)
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
        registerAll(
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
//                DeleteClipCommand.class,
                ListCommandsCommand.class
        );
        worker = injector.getInstance(SqsPollingWorker.class);
        worker.start();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        DiscordActions actions = buildActions(event);
        try {
            registry.execute(actions);
        } catch (Exception ex) {
            actions.send("Oh no, I'm dying!");
            logger.error("Error processing guild message", ex);
        }
        super.onGuildMessageReceived(event);
    }

    private DiscordActions buildActions(GuildMessageReceivedEvent event) {
        AudioContext context = new AudioContext(event.getChannel(), getGuildPlayer(event.getGuild()), playerManager);
        return new JdaDiscordActions(event, context);
    }

    @SafeVarargs
    private void registerAll(Class<? extends DiscordCommand>... commands) {
        for (var command: commands) {
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
