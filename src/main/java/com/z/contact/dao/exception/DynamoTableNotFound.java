package com.z.contact.dao.exception;

import static com.z.contact.environment.DynamoEnvironment.DYNAMO_TABLE_NAME;

public class DynamoTableNotFound extends Exception {
    public DynamoTableNotFound() {
        super("Must define env var: "+ DYNAMO_TABLE_NAME);
    }
}
