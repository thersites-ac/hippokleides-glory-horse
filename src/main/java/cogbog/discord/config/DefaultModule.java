package cogbog.discord.config;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.command.audio.*;
import cogbog.discord.command.general.*;
import cogbog.discord.command.pathfinder.FeatCommand;
import cogbog.discord.command.pathfinder.SpellCommand;
import cogbog.discord.service.AuthConfigService;
import cogbog.discord.service.AuthManager;
import cogbog.discord.service.impl.AuthConfigServiceImpl;
import cogbog.discord.service.impl.PersistenceAuthManagerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

import javax.inject.Named;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cogbog.discord.constants.Names.*;

public class DefaultModule extends AbstractModule {
    private static final String COMMANDS_PROPERTIES_FILE = "commands.properties";

    @Override
    protected void configure() {
        bind(AuthManager.class).to(PersistenceAuthManagerImpl.class);
        bind(AuthConfigService.class).to(AuthConfigServiceImpl.class);
        bind(AudioPlayerManager.class).to(DefaultAudioPlayerManager.class);
        install(new EmbedModule());
        install(new ServicesModule());
    }

    @Provides
    @Singleton
    ExecutorService executorService(@Named(CORE_POOL_SIZE) int corePoolSize,
                                    @Named(MAX_POOL_SIZE) int maxPoolSize,
                                    @Named(KEEP_ALIVE_TIME) int keepAliveTime) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100, true),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Provides
    @Named(MAX_POOL_SIZE)
    int maxPoolSize() {
        return 50;
    }

    @Provides
    @Named(CORE_POOL_SIZE)
    int corePoolSize() {
        return 20;
    }

    @Provides
    @Named(KEEP_ALIVE_TIME)
    int keepAliveTime() {
        return 30000;
    }

    @Provides
    // fixme: gross
    Set<Class<? extends DiscordCommand>> commands() {
        try {
            var properties = new Properties();
            properties.load(getClass().getResourceAsStream(COMMANDS_PROPERTIES_FILE));
            return properties.entrySet().stream()
                    .map(e -> {
                        try {
                            return ((String) e.getValue()).equalsIgnoreCase("true")?
                                    getClass().getClassLoader()
                                            .loadClass((String) e.getKey()):
                                    null;
                        } catch (ClassNotFoundException classNotFoundException) {}
                        return null;
                    })
                    .filter(clazz -> clazz != null && DiscordCommand.class.isAssignableFrom(clazz))
                    .map(clazz -> (Class<? extends DiscordCommand>) clazz)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            // fixme
            e.printStackTrace();
        }
        return Collections.emptySet();
    }
}
