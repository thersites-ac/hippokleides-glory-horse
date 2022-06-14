package net.picklepark.discord.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.dv8tion.jda.api.JDA;
import net.picklepark.discord.service.AuthConfigService;
import net.picklepark.discord.service.AuthManager;
import net.picklepark.discord.service.impl.AuthConfigServiceImpl;
import net.picklepark.discord.service.impl.AuthManagerImpl;

public class DefaultModule extends AbstractModule {

    private final JDA jda;

    public DefaultModule(JDA jda) {
        this.jda = jda;
    }

    @Override
    protected void configure() {
        bind(AuthManager.class).to(AuthManagerImpl.class);
        bind(AuthConfigService.class).to(AuthConfigServiceImpl.class);
        install(new EmbedModule());
        install(new ServicesModule());
    }

    @Provides JDA jda() {
        return jda;
    }
}
