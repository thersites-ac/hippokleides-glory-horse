package net.picklepark.discord.adaptor;

import java.util.List;
import java.util.Map;

public interface DataPersistenceAdaptor<T> {
    void write(String table, T object);
    // fixme: this may eventually need to be a stream
    T read(String table, Map<String, String> key);
}
