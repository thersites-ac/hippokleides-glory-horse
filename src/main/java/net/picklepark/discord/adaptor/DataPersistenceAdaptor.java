package net.picklepark.discord.adaptor;

import net.picklepark.discord.exception.DataMappingException;

import java.util.Map;

public interface DataPersistenceAdaptor<T> {
    void write(T object);
    T read(Map<String, String> key) throws DataMappingException;
}
