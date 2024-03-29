package cogbog.discord.persistence;

import cogbog.discord.exception.DataMappingException;

import java.util.Map;

public interface MappingFactory<T> {
    Map<String, String> toMap(T object);
    T fromMap(Map<String, String> map) throws DataMappingException;
    String getTable();
}
