package com.z.contact;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dao.ContactRepository;
import com.z.contact.domain.Contact;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.transformer.AddContactRequestTransformer;
import com.z.contact.transformer.ContactResponseTransformer;
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
    private final ContactResponseTransformer contactResponseTransformer;
    private final ContactRepository contactRepository;
    private final ObjectMapper objectMapper;
    private final Map<Pair<SdkHttpMethod, String>, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> routerMap;

    public ContactProxyLambda(AddContactRequestTransformer addContactRequestTransformer,
                              ContactResponseTransformer contactResponseTransformer,
                              ContactRepository contactRepository) {
        this.addContactRequestTransformer = addContactRequestTransformer;
        this.contactResponseTransformer = contactResponseTransformer;
        this.contactRepository = contactRepository;
        this.routerMap = Map.of(
                Pair.of(SdkHttpMethod.POST, "/proxyzTest"), addContact(),
                Pair.of(SdkHttpMethod.GET, "/proxyzTest/{contactId}"), findContactById()
        );
        objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        log.info("Request {} {}", input.getHttpMethod(), input.getPath());
        log.info("Headers: {}", input.getMultiValueHeaders());
        log.info("Path parameters: {}", input.getPathParameters());
        log.info("Query parameters: {}", input.getMultiValueQueryStringParameters());
        log.info("Body: {}", input.getBody());
        return this.routerMap
                .getOrDefault(getKey(input), this.badRequest())
                .handleRequest(input, context);
    }

    private RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> findContactById() {
        return ((input, context) -> {
            String id = input.getPathParameters().getOrDefault("contactId", "");
            log.info("FindById request: {}", id);
            Contact contact = contactRepository.findById(id);
            try {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(HttpStatusCode.OK)
                        .withBody(objectMapper.writeValueAsString(contactResponseTransformer.apply(contact)));
            } catch (JsonProcessingException e) {
                log.error(e.getLocalizedMessage());
                return badRequestResponse(e.getLocalizedMessage());
            }
        });
    }

    private RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> addContact() {
        return ((input, context) -> {
            try {
                AddContactRequest request = objectMapper.readValue(input.getBody(), AddContactRequest.class);
                log.info("Add contact request: {}", request);
                Contact contact = addContactRequestTransformer.apply(request);
                contactRepository.put(contact);
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(HttpStatusCode.CREATED)
                        .withBody(objectMapper.writeValueAsString(contactResponseTransformer.apply(contact)));
            } catch (JsonProcessingException e) {
                log.error(e.getLocalizedMessage());
                return badRequestResponse(e.getLocalizedMessage());
            }
        });
    }

    private Pair<SdkHttpMethod, String> getKey(APIGatewayProxyRequestEvent input) {
        Map<String, String> parameters = input.getPathParameters();
        String path = input.getPath();

        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                path = path.replace(entry.getValue(), String.format("{%s}", entry.getKey()));
            }
            log.info("Path with parameters: {}", path);
        }

        return Pair.of(SdkHttpMethod.fromValue(input.getHttpMethod()), path);
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
