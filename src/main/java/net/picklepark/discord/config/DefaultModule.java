package net.picklepark.discord.config;

import com.google.inject.AbstractModule;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.picklepark.discord.service.AuthConfigService;
import net.picklepark.discord.service.AuthManager;
import net.picklepark.discord.service.impl.AuthConfigServiceImpl;
import net.picklepark.discord.service.impl.AuthManagerImpl;
import net.picklepark.discord.service.impl.PersistenceAuthManagerImpl;

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
