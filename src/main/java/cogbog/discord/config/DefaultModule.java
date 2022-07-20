package cogbog.discord.config;

import cogbog.discord.Bot;
import cogbog.discord.command.DiscordCommand;
import cogbog.discord.service.AuthManager;
import cogbog.discord.service.impl.PersistenceAuthManagerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class DefaultModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(DefaultModule.class);
    private static final String ENV = System.getProperty("env");
    private static final String COMMANDS_PROPERTIES_FILE = ENV + "/commands.properties";
    private static final String CONSTANTS_PROPERTIES_FILE = ENV + "/constants.properties";
    static {
        logger.info("env: " + ENV);
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), loadProperties());
        bind(AuthManager.class).to(PersistenceAuthManagerImpl.class);
        bind(AudioPlayerManager.class).to(DefaultAudioPlayerManager.class);
        install(new EmbedModule());
        install(new ServicesModule());
    }

    @Provides
    @Singleton
    ExecutorService executorService(@Named("core.pool.size") int corePoolSize,
                                    @Named("max.pool.size") int maxPoolSize,
                                    @Named("keep.alive.time") int keepAliveTime) {
        var threadFactory = new ThreadFactory() {
            int id = 0;
            @Override
            public Thread newThread(@NotNull Runnable runnable) {
                id++;
                return new Thread(runnable, "bot-event-handler-" + id);
            }
        };
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100, true),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Provides
    // fixme: gross
    Set<Class<? extends DiscordCommand>> commands() {
        try {
            var properties = new Properties();
            var stream = getClass().getResourceAsStream(COMMANDS_PROPERTIES_FILE);
            if (stream == null)
                badPropertiesFile(COMMANDS_PROPERTIES_FILE);
            properties.load(stream);
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

    @Provides
    @Singleton
    JDA jda(Bot bot) throws LoginException {
        return JDABuilder.create(System.getProperty("token"), GUILD_MESSAGES, GUILD_VOICE_STATES, GUILD_MEMBERS)
                .addEventListeners(bot)
                .build();
    }

    private Properties loadProperties() {
        var result = new Properties();
        try {
            var stream = getClass().getResourceAsStream(CONSTANTS_PROPERTIES_FILE);
            if (stream == null)
                badPropertiesFile(CONSTANTS_PROPERTIES_FILE);
            else
                result.load(stream);
        } catch (IOException e) {
            logger.error("Could not load properties from file", e);
            System.exit(1);
        }
        return result;
    }

    private void badPropertiesFile(String filename) {
        logger.error("No properties located at " + filename);
        System.exit(1);
    }
}
