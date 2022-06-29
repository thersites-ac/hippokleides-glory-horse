package net.picklepark.discord.exception;

import java.util.Map;

public class DataMappingException extends Exception {
    public DataMappingException(Map<String, String> map, Exception ex) {
        super(map.toString(), ex);
    }

    public DataMappingException(Map<String, String> map) {
        super(map.toString());
    }
}
