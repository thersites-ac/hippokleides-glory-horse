package tools;

import cogbog.discord.service.AuthConfigService;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TestConfigService implements AuthConfigService {
    private ConcurrentHashMap<String, Set<Long>> admins;

    public TestConfigService() {
        admins = new ConcurrentHashMap<>();
    }

    @Override
    public ConcurrentHashMap<String, Set<Long>> getCurrentAdmins() {
        return new ConcurrentHashMap<>(Collections.unmodifiableMap(admins));
    }

    @Override
    public void persistAdmins(Map<String, Set<Long>> admins) {
        this.admins = new ConcurrentHashMap<>(admins);
    }
}
