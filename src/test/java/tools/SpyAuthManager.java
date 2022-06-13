package tools;

import net.picklepark.discord.service.AuthConfigService;
import net.picklepark.discord.service.impl.AuthManagerImpl;

import java.util.Set;

public class SpyAuthManager extends AuthManagerImpl {
    public SpyAuthManager(AuthConfigService configService) {
        super(configService);
    }

    public Set<Long> getAdminsFor(String guild) {
        return admins.get(guild);
    }
}
