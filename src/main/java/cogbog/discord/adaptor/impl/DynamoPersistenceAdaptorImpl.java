package cogbog.discord.adaptor.impl;

import cogbog.discord.adaptor.DataPersistenceAdaptor;
import cogbog.discord.exception.DataMappingException;
import cogbog.discord.persistence.MappingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

// TODO: handle runtime exceptions from the Dynamo client
public class DynamoPersistenceAdaptorImpl<T> implements DataPersistenceAdaptor<T> {

    private static final Logger logger = LoggerFactory.getLogger(DynamoPersistenceAdaptorImpl.class);

    private final DynamoDbClient client;
    private final MappingFactory<T> factory;

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

    // todo: can I make the key a type derivable from `T` to avoid passing around raw maps?
    @Override
    public T read(Map<String, String> key) throws DataMappingException {
        logger.info("Reading from dynamo table " + factory.getTable() + " with key " + key);
        var request = GetItemRequest.builder()
                .tableName(factory.getTable())
                .key(wrap(key))
                .build();
        var result = unwrap(client.getItem(request).item());
        return result.isEmpty()? null: factory.fromMap(result);
    }

    private Map<String, AttributeValue> wrap(Map<String, String> data) {
        Map<String, AttributeValue> result = new HashMap<>();
        data.forEach((key, value) -> result.put(key, AttributeValue.builder().s(value).build()));
        return result;
    }

    private Map<String, String> unwrap(Map<String, AttributeValue> data) {
        logger.info("Dynamo sent data: " + data);
        Map<String, String> result = new HashMap<>();
        data.forEach((key, value) -> result.put(key, value.s()));
        return result;
    }
}
