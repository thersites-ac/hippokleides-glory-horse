package tools;

import net.picklepark.discord.service.AuthConfigService;
import net.picklepark.discord.service.impl.AuthManagerImpl;
import net.picklepark.discord.service.impl.JavaConfigManager;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Set;

public class SpyAuthManager extends AuthManagerImpl {
    public SpyAuthManager(AuthConfigService configService) {
        super(configService, new NoopJavaConfigManager<>());
    }

    public Set<Long> getAdminsFor(String guild) {
        return admins.get(guild);
    }

    private static class NoopJavaConfigManager<T> extends JavaConfigManager<T> {

        public NoopJavaConfigManager() {
            super(null, null, null);
        }

        private NoopJavaConfigManager(String configBucket, S3Client configFetcher, String configKey) {
            super(configBucket, configFetcher, configKey);
        }

        @Override
        public T getRemote() {
            return null;
        }

        @Override
        public void persist(T object) {}
    }
}
