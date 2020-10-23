package com.z.contact;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dao.ContactRepository;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.transformer.AddContactRequestTransformer;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.utils.Pair;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class ContactProxyLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AddContactRequestTransformer addContactRequestTransformer;
    private final ContactRepository contactRepository;
    private final ObjectMapper objectMapper;
    private final Map<Pair<SdkHttpMethod,String>, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> routerMap;

    public ContactProxyLambda(AddContactRequestTransformer addContactRequestTransformer,
                              ContactRepository contactRepository) {
        this.addContactRequestTransformer = addContactRequestTransformer;
        this.contactRepository = contactRepository;
        this.routerMap = Map.of(
                Pair.of(SdkHttpMethod.POST, "/proxyzTest"), addContact()
        );
        objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        log.info("RequestId: {} - {} {}", context.getAwsRequestId(), input.getHttpMethod(), input.getPath());
        return this.routerMap
                .getOrDefault(getKey(input), this.badRequest())
                .handleRequest(input, context);
    }

    private RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> addContact() {
        return ((input, context) -> {
            try {
                AddContactRequest  request = objectMapper.readValue(input.getBody(), AddContactRequest.class);
                log.info("Add contact request: {}", request);
                contactRepository.put(addContactRequestTransformer.buildContact(request));
                return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.CREATED);
            } catch (JsonProcessingException e) {
                log.error(e.getLocalizedMessage());
                return badRequestResponse(e.getLocalizedMessage());
            }
        });
    }

    private Pair<SdkHttpMethod, String> getKey(APIGatewayProxyRequestEvent input) {
        return Pair.of(SdkHttpMethod.fromValue(input.getHttpMethod()), input.getPath());
    }

    private RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> badRequest() {
        return (input, context) -> badRequestResponse("unsupported method/path");
    }

    private APIGatewayProxyResponseEvent badRequestResponse(String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatusCode.BAD_REQUEST)
                .withBody(body);
    }
}
