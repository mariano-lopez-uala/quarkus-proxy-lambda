package com.z.contact.router;

import com.z.contact.dto.AddContactRequest;
import com.z.contact.dto.ContactResponse;
import com.z.contact.service.ContactService;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

@ApplicationScoped
@OpenAPIDefinition(info = @Info(title="Contact example API", version = "1.0.0"))
public class ContactRouter {
    private final String BASE_PATH = "/proxyzTest";
    private final String JSON_MEDIA_TYPE = "application/json";
    private final ContactService contactService;

    public ContactRouter(ContactService contactService) {
        this.contactService = contactService;
    }

    @Route(methods = POST, path = BASE_PATH, produces = JSON_MEDIA_TYPE, consumes = JSON_MEDIA_TYPE)
    ContactResponse add(@Body @RequestBody AddContactRequest request, RoutingContext context) {
        ContactResponse response = this.contactService.add(request);
        context.response().setStatusCode(HttpStatusCode.CREATED);
        return response;
    }

    @Route(methods = GET, path = BASE_PATH + "/:contactId", produces = JSON_MEDIA_TYPE)
    ContactResponse findById(RoutingContext context, @Param("contactId") String contactId) {
        Optional<ContactResponse> response = this.contactService.findById(contactId);
        if (response.isPresent()) {
            context.response().setStatusCode(HttpStatusCode.OK);
            return response.get();
        } else {
            context.response().setStatusCode(HttpStatusCode.NO_CONTENT);
            return null;
        }
    }
}
