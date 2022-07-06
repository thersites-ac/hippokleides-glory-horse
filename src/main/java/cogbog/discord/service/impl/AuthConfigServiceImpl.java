package cogbog.discord.service.impl;

import cogbog.discord.service.AuthConfigService;
import software.amazon.awssdk.services.s3.S3Client;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.*;
import java.util.Map;
import java.util.Set;

import static cogbog.discord.constants.Names.S3_BUCKET_CONFIG;
import static cogbog.discord.constants.Names.S3_CONFIG_CLIENT;

@Singleton
public class AuthConfigServiceImpl extends JavaConfigManager<Map<String, Set<Long>>> implements AuthConfigService {

    private static final String CONFIG_KEY = "discord-bot-auth-config";

    @Inject
    public AuthConfigServiceImpl(@Named(S3_BUCKET_CONFIG) String configBucket,
                                 @Named(S3_CONFIG_CLIENT) S3Client configFetcher) {
        super(configBucket, configFetcher, CONFIG_KEY);
    }

    @Override
    public Map<String, Set<Long>> getCurrentAdmins() throws IOException {
        return getRemote();
    }

    @Override
    public void persistAdmins(Map<String, Set<Long>> admins) throws IOException {
        persist(admins);
    }
}
