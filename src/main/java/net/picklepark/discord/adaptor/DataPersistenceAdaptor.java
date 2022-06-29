package net.picklepark.discord.adaptor;

import java.util.List;
import java.util.Map;

public interface DataPersistenceAdaptor<T> {
    void write(T object);
    // fixme: this may eventually need to be a stream
    T read(Map<String, String> key);
}
