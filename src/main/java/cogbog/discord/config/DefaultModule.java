package cogbog.discord.config;

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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cogbog.discord.constants.Names.*;

public class DefaultModule extends AbstractModule {
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
}
