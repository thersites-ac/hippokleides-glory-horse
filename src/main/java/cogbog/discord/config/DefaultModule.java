package cogbog.discord.config;

import cogbog.discord.command.DiscordCommand;
import cogbog.discord.service.AuthConfigService;
import cogbog.discord.service.AuthManager;
import cogbog.discord.service.impl.AuthConfigServiceImpl;
import cogbog.discord.service.impl.PersistenceAuthManagerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(DefaultModule.class);
    private static final String COMMANDS_PROPERTIES_FILE = "qa/commands.properties";
    private static final String CONSTANTS_PROPERTIES_FILE = "qa/constants.properties";

    @Override
    protected void configure() {
        Names.bindProperties(binder(), loadProperties());
        bind(AuthManager.class).to(PersistenceAuthManagerImpl.class);
        bind(AuthConfigService.class).to(AuthConfigServiceImpl.class);
        bind(AudioPlayerManager.class).to(DefaultAudioPlayerManager.class);
        install(new EmbedModule());
        install(new ServicesModule());
    }

    private Properties loadProperties() {
        var result = new Properties();
        try {
            var stream = getClass().getResourceAsStream(CONSTANTS_PROPERTIES_FILE);
            result.load(stream);
        } catch (IOException e) {
            logger.error("Could not load properties from file", e);
            System.exit(1);
        }
        return result;
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
    // fixme: gross
    Set<Class<? extends DiscordCommand>> commands() {
        try {
            var properties = new Properties();
            properties.load(getClass().getResourceAsStream(COMMANDS_PROPERTIES_FILE));
            List<Throwable> exceptions = new ArrayList<>();
            Set<Class<? extends DiscordCommand>> result = properties.entrySet().stream()
                    .map(e -> {
                        try {
                            return ((String) e.getValue()).equalsIgnoreCase("true")?
                                    getClass().getClassLoader()
                                            .loadClass((String) e.getKey()):
                                    null;
                        } catch (ClassNotFoundException ex) {
                            exceptions.add(ex);
                        }
                        return null;
                    })
                    .filter(clazz -> clazz != null && DiscordCommand.class.isAssignableFrom(clazz))
                    .map(clazz -> (Class<? extends DiscordCommand>) clazz)
                    .collect(Collectors.toSet());
            if (exceptions.isEmpty()) {
                return result;
            } else {
                exceptions.forEach(e -> logger.error("While loading class", e));
                System.exit(1);
            }
        } catch (IOException e) {
            logger.error("While reading commands.properties", e);
            System.exit(1);
        }
        return Collections.emptySet();
    }
}
