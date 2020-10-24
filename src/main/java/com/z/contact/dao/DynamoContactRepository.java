package com.z.contact.dao;

import com.z.contact.dao.exception.DynamoTableNotFound;
import com.z.contact.domain.Contact;
import com.z.contact.domain.Status;
import com.z.contact.environment.DynamoEnvironment;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class DynamoContactRepository implements ContactRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String TABLE_NAME;

    public DynamoContactRepository(DynamoDbClient dynamoDbClient) throws DynamoTableNotFound {
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

    @Override
    public Optional<Contact> findById(String id) {
        GetItemRequest request = GetItemRequest.builder()
                .key(Map.of(
                    "id", AttributeValue.builder().s(id).build()
                ))
                .tableName(TABLE_NAME)
                .build();
        GetItemResponse response = this.dynamoDbClient.getItem(request);
        Optional<Contact> contact = Optional.empty();
        if (response.hasItem()) {
            log.info("Dynamo ItemResponse: {}", response.item());
            contact = Optional.of(
                    Contact.builder()
                    .id(response.item().get("id").s())
                    .firstName(response.item().get("firstName").s())
                    .lastName(response.item().get("lastName").s())
                    .status(Status.valueOf(response.item().get("status").s()))
                    .build()
            );
            log.info("Contact UnMarshaling: {}", contact);
        }
        return contact;
    }

    private String getTableFromEnv() throws DynamoTableNotFound {
        return Optional.ofNullable(System.getenv(DynamoEnvironment.DYNAMO_TABLE_NAME.name()))
                .orElseThrow(DynamoTableNotFound::new);
    }
}
