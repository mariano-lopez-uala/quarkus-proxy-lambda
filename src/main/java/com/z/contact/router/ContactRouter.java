package com.z.contact.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.dto.ContactResponse;
import com.z.contact.service.ContactService;
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
    private final String BASE_PATH = "/proxyzTest";
    private final String JSON_MEDIA_TYPE = "application/json";
    private final ContactService contactService;
    private final ObjectMapper objectMapper;

    public ContactRouter(ContactService contactService,
                         ObjectMapper objectMapper) {
        this.contactService = contactService;
        this.objectMapper = objectMapper;
    }

    @Route(methods = POST, path = BASE_PATH, produces = JSON_MEDIA_TYPE, consumes = JSON_MEDIA_TYPE)
    void add(RoutingContext context, @Body AddContactRequest request) {
        ContactResponse response = this.contactService.add(request);
        context.response().setStatusCode(HttpStatusCode.CREATED);
        context.response().end(this.toBuffer(response));
    }

    @Route(methods = GET, path = BASE_PATH + "/:contactId", produces = JSON_MEDIA_TYPE)
    void findById(RoutingContext context, @Param("contactId") String contactId) {
        this.contactService
                .findById(contactId)
                .ifPresentOrElse(contactResponse -> {
                    context.response().setStatusCode(HttpStatusCode.OK);
                    context.response().end(this.toBuffer(contactResponse));
                }, () -> {
                    context.response().setStatusCode(HttpStatusCode.NO_CONTENT);
                    context.response().end();
                });

    }

    @SneakyThrows(IOException.class)
    private Buffer toBuffer(Object obj) {
        return Buffer.buffer(objectMapper.writeValueAsBytes(obj));
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
