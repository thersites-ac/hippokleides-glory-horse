package tools;

import net.picklepark.discord.adaptor.DataPersistenceAdaptor;
import net.picklepark.discord.model.AuthRecord;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.picklepark.discord.service.impl.PersistenceAuthManagerImpl.GUILD_ID;
import static net.picklepark.discord.service.impl.PersistenceAuthManagerImpl.USER_ID;

public class InMemoryAuthPersistenceAdaptor implements DataPersistenceAdaptor<AuthRecord> {
    private final Set<AuthRecord> data;

    public InMemoryAuthPersistenceAdaptor() {
        data = new HashSet<>();
    }

    @Override
    public void write(AuthRecord object) {
        data.add(object);
    }

    @Override
    public AuthRecord read(Map<String, String> key) {
        return data.stream()
                .filter(record ->
                    key.get(GUILD_ID).equals(record.getGuildId()) &&
                    key.get(USER_ID).equals(record.getUserId() + "")
                ).findFirst()
                .orElse(null);
    }
}
