package net.picklepark.discord.adaptor.impl;

import com.google.inject.Inject;
import net.picklepark.discord.adaptor.DataPersistenceAdaptor;
import net.picklepark.discord.persistence.MappingFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class DynamoPersistenceAdaptorImpl<T> implements DataPersistenceAdaptor<T> {

    private final DynamoDbClient client;
    private final MappingFactory<T> factory;

    // fixme: not added to any module yet
    @Inject
    public DynamoPersistenceAdaptorImpl(DynamoDbClient client, MappingFactory<T> factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public void write(T object) {
        var request = PutItemRequest.builder()
                .tableName(factory.getTable())
                .item(wrap(factory.toMap(object)))
                .build();
        client.putItem(request);
    }

    @Override
    public T read(Map<String, String> key) {
        var request = GetItemRequest.builder()
                .tableName(factory.getTable())
                .key(wrap(key))
                .build();
        var result = unwrap(client.getItem(request).item());
        return factory.fromMap(result);
    }

    private Map<String, AttributeValue> wrap(Map<String, String> data) {
        Map<String, AttributeValue> result = new HashMap<>();
        data.forEach((key, value) -> result.put(key, AttributeValue.builder().s(value).build()));
        return result;
    }

    private Map<String, String> unwrap(Map<String, AttributeValue> data) {
        Map<String, String> result = new HashMap<>();
        data.forEach((key, value) -> result.put(key, value.s()));
        return result;
    }
}
