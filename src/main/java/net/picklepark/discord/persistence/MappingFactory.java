package net.picklepark.discord.persistence;

import java.util.Map;

public interface MappingFactory<T> {
    Map<String, String> toMap(T object);
    T fromMap(Map<String, String> map);
    String getTable();
}
