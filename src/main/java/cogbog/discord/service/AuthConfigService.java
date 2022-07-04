package cogbog.discord.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface AuthConfigService {
    Map<String, Set<Long>> getCurrentAdmins() throws IOException;
    void persistAdmins(Map<String, Set<Long>> admins) throws IOException;
}
