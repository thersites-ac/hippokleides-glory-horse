package net.picklepark.discord.config;

import com.google.inject.AbstractModule;

public class DefaultModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new EmbedModule());
        install(new ServicesModule());
    }
}
