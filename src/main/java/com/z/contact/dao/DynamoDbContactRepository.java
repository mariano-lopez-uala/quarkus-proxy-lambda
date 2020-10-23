package com.z.contact.dao;

import com.z.contact.dao.exception.DynamoTableNotFound;
import com.z.contact.domain.Contact;
import com.z.contact.environment.DynamoEnvironment;
import com.z.contact.utils.EnvironmentUtils;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class DynamoDbContactRepository implements ContactRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String TABLE_NAME;

    public DynamoDbContactRepository(DynamoDbClient dynamoDbClient) throws DynamoTableNotFound {
        this.dynamoDbClient = dynamoDbClient;
        this.TABLE_NAME = getTableFromEnv();
    }

    @Override
    public void put(Contact contact) {
        PutItemRequest itemRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(Map.of(
                    "id", AttributeValue.builder().s(contact.getId()).build(),
                    "firstName", AttributeValue.builder().s(contact.getFirstName()).build(),
                    "lastName", AttributeValue.builder().s(contact.getLastName()).build(),
                    "status", AttributeValue.builder().s(contact.getStatus().name()).build()
                ))
                .build();
        this.dynamoDbClient.putItem(itemRequest);
    }

    private String getTableFromEnv() throws DynamoTableNotFound {
        return EnvironmentUtils
                .getEnvironmentVariable(DynamoEnvironment.DYNAMO_TABLE_NAME.name())
                .orElseThrow(DynamoTableNotFound::new);
    }
}
