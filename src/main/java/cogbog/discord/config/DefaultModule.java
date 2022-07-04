package cogbog.discord.config;

import cogbog.discord.service.AuthConfigService;
import cogbog.discord.service.AuthManager;
import cogbog.discord.service.impl.AuthConfigServiceImpl;
import cogbog.discord.service.impl.PersistenceAuthManagerImpl;
import com.google.inject.AbstractModule;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

public class DefaultModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AuthManager.class).to(PersistenceAuthManagerImpl.class);
        bind(AuthConfigService.class).to(AuthConfigServiceImpl.class);
        bind(AudioPlayerManager.class).to(DefaultAudioPlayerManager.class);
        install(new EmbedModule());
        install(new ServicesModule());
    }
}
