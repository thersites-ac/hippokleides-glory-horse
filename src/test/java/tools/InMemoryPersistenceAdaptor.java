package tools;

import net.picklepark.discord.adaptor.DataPersistenceAdaptor;
import net.picklepark.discord.model.AuthRecord;
import net.picklepark.discord.model.WelcomeRecord;
import net.picklepark.discord.persistence.AuthRecordMappingFactory;
import net.picklepark.discord.persistence.WelcomeRecordMappingFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InMemoryPersistenceAdaptor<T> implements DataPersistenceAdaptor<T> {
    private final Set<T> data;
    private final KeyMatcher<T> matcher;

    public InMemoryPersistenceAdaptor(KeyMatcher<T> matcher) {
        data = new HashSet<>();
        this.matcher = matcher;
    }

    @Override
    public void write(T object) {
        data.add(object);
    }

    @Override
    public T read(Map<String, String> key) {
        return data.stream()
                .filter(record -> matcher.match(key, record))
                .findFirst()
                .orElse(null);
    }

    public interface KeyMatcher<T> {
        boolean match(Map<String, String> key, T object);
    }

    public static final KeyMatcher<AuthRecord> AUTH_RECORD_KEY_MATCHER = (key, record) ->
            record.getGuildId().equals(key.get(AuthRecordMappingFactory.GUILD_ID)) &&
                    (record.getUserId() + "").equals(key.get(AuthRecordMappingFactory.USER_ID));

    public static final KeyMatcher<WelcomeRecord> WELCOME_RECORD_KEY_MATCHER = (key, record) ->
            record.getGuildId().equals(key.get(WelcomeRecordMappingFactory.GUILD_ID)) &&
                    (record.getUserId() + "").equals(key.get(WelcomeRecordMappingFactory.USER_ID));

    public static DataPersistenceAdaptor<AuthRecord> forAuthRecords() {
        return new InMemoryPersistenceAdaptor<>(AUTH_RECORD_KEY_MATCHER);
    }

    public static DataPersistenceAdaptor<WelcomeRecord> forWelcomeRecords() {
        return new InMemoryPersistenceAdaptor<>(WELCOME_RECORD_KEY_MATCHER);
    }
}
