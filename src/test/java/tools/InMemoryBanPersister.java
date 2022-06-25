package tools;

import net.picklepark.discord.service.impl.JavaConfigManager;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InMemoryBanPersister extends JavaConfigManager<Map<String, Set<Long>>> {

    private static Map<String, Set<Long>> data;

    public InMemoryBanPersister() {
        super(null, null, null);
        if (data == null)
            data = new HashMap<>();
    }

    @Override
    public Map<String, Set<Long>> getRemote() throws IOException {
        Map<String, Set<Long>> map = new HashMap<>();
        data.forEach((key, value) -> map.put(key, new HashSet<>(value)));
        return map;
    }

    @Override
    public void persist(Map<String, Set<Long>> object) throws IOException {
        data = object;
    }

    public void reset() {
        data = new HashMap<>();
    }
}
