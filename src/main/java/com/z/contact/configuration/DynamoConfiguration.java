package com.z.contact.configuration;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "dynamodb")
public interface DynamoConfiguration {
    String tableName();
}
