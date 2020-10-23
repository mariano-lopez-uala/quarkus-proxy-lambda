package com.z.contact.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dao.ContactRepository;
import com.z.contact.domain.Contact;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.dto.ContactResponse;
import com.z.contact.transformer.AddContactRequestTransformer;
import com.z.contact.transformer.ContactResponseTransformer;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.http.HttpStatusCode;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

@ApplicationScoped
@Slf4j
public class ContactRouter {
    private final AddContactRequestTransformer addContactRequestTransformer;
    private final ContactResponseTransformer contactResponseTransformer;
    private final ContactRepository contactRepository;
    private final ObjectMapper objectMapper;

    public ContactRouter(AddContactRequestTransformer addContactRequestTransformer,
                         ContactResponseTransformer contactResponseTransformer,
                         ContactRepository contactRepository,
                         ObjectMapper objectMapper) {
        this.addContactRequestTransformer = addContactRequestTransformer;
        this.contactResponseTransformer = contactResponseTransformer;
        this.contactRepository = contactRepository;
        this.objectMapper = objectMapper;
    }

    @Route(methods = POST, path = "/proxyzTest", produces = "application/json", consumes = "application/json")
    @SneakyThrows(IOException.class)
    void add(RoutingContext context, @Body AddContactRequest request) {
            log.info("Add contact request: {}", request);
            Contact contact = addContactRequestTransformer.apply(request);
            contactRepository.put(contact);
            ContactResponse response = contactResponseTransformer.apply(contact);
            context.response().setStatusCode(HttpStatusCode.CREATED);
            context.response().end(Buffer.buffer(objectMapper.writeValueAsBytes(response)));
    }

    @Route(methods = GET, path = "/proxyzTest/:accountId", produces = "application/json")
    @SneakyThrows(IOException.class)
    void findById(RoutingContext context, @Param("accountId") String accountId) {
        log.info("FindById: {}", accountId);
        Contact contact = contactRepository.findById(accountId);
        ContactResponse response = contactResponseTransformer.apply(contact);
        context.response().setStatusCode(HttpStatusCode.OK);
        context.response().end(Buffer.buffer(objectMapper.writeValueAsBytes(response)));
    }


    @Route(type = Route.HandlerType.FAILURE)
    void handleIOException(IOException e, HttpServerResponse response) {
        response.setStatusCode(HttpStatusCode.BAD_REQUEST).end(e.getLocalizedMessage());
    }

    @RouteFilter(100)
    void logRequestFilter(RoutingContext context) {
        log.info("Request {} {}", context.request().rawMethod(), context.request().path());
        log.info("Headers: {}", context.request().headers());
        log.info("Path parameters: {}", context.request().params());
        log.info("Query parameters: {}", context.request().query());

        context.next();
    }
}
