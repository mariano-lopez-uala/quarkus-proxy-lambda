package com.z.contact;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dao.ContactRepository;
import com.z.contact.domain.Contact;
import com.z.contact.domain.Status;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.transformer.AddContactRequestTransformer;
import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.http.SdkHttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@QuarkusTest
public class LambdaHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMock
    ContactRepository mockContactRepository;
    @InjectMock
    AddContactRequestTransformer mockAddContactRequestTransformer;

    @Test
    public void shouldAddContactWhenPostProxy() throws Exception {
        AddContactRequest addContactRequest = new AddContactRequest("Mariano", "Lopez");
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(SdkHttpMethod.POST.name())
                .withPath("/proxyzTest")
                .withBody(objectMapper.writeValueAsString(addContactRequest));
        Contact contact = Contact.builder().id("ID").firstName("Mariano").lastName("Lopez").status(Status.CREATED).build();

        when(mockAddContactRequestTransformer.buildContact(addContactRequest)).thenReturn(contact);
        doNothing().when(mockContactRepository).put(contact);

        APIGatewayProxyResponseEvent out = LambdaClient.invoke(APIGatewayProxyResponseEvent.class, requestEvent);

        Mockito.verify(mockContactRepository).put(contact);
        assertEquals(201, out.getStatusCode());
        assertEquals(addContactRequest.getFirstName(), contact.getFirstName());
        assertEquals(addContactRequest.getLastName(), contact.getLastName());
        assertEquals(Status.CREATED, contact.getStatus());
        assertNotNull(contact.getId());
    }

    @Test
    public void shouldGetBadRequestWhenUnsupportedMethodProxy() {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(SdkHttpMethod.DELETE.name())
                .withPath("/");

        APIGatewayProxyResponseEvent out = LambdaClient.invoke(APIGatewayProxyResponseEvent.class, requestEvent);

        Mockito.verifyNoInteractions(mockContactRepository, mockAddContactRequestTransformer);
        assertEquals(400, out.getStatusCode());
        assertEquals("unsupported method/path", out.getBody());
    }

}
