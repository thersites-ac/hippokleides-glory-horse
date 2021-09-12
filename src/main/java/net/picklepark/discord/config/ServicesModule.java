package net.picklepark.discord.config;

import com.google.inject.AbstractModule;
import net.picklepark.discord.service.PollingService;
import net.picklepark.discord.service.RecordingService;
import net.picklepark.discord.service.StorageService;
import net.picklepark.discord.service.impl.AwsStorageService;
import net.picklepark.discord.service.impl.LocalRecordingService;
import net.picklepark.discord.service.impl.SqsPollingService;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StorageService.class).to(AwsStorageService.class);
        bind(PollingService.class).to(SqsPollingService.class);
        bind(RecordingService.class).to(LocalRecordingService.class);
    }
}
