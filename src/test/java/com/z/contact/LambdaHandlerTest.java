package com.z.contact;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dao.ContactRepository;
import com.z.contact.domain.Contact;
import com.z.contact.domain.Status;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.dto.ContactResponse;
import com.z.contact.transformer.AddContactRequestTransformer;
import com.z.contact.transformer.ContactResponseTransformer;
//import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.http.SdkHttpMethod;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class LambdaHandlerTest {
    /*private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMock
    ContactRepository mockContactRepository;
    @InjectMock
    AddContactRequestTransformer mockAddContactRequestTransformer;
    @InjectMock
    ContactResponseTransformer mockContactResponseTransformer;

    @Test
    public void shouldAddContactWhenPostProxy() throws Exception {
        AddContactRequest addContactRequest = new AddContactRequest("Mariano", "Lopez");
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(SdkHttpMethod.POST.name())
                .withPath("/proxyzTest")
                .withBody(objectMapper.writeValueAsString(addContactRequest));
        Contact contact = Contact.builder().id("ID").firstName("Mariano").lastName("Lopez").status(Status.CREATED).build();
        ContactResponse contactResponse = ContactResponse.builder().id("ID").firstAndLastName("Mariano, Lopez").status("CREATED").build();

        when(mockAddContactRequestTransformer.apply(addContactRequest)).thenReturn(contact);
        doNothing().when(mockContactRepository).put(contact);
        when(mockContactResponseTransformer.apply(contact)).thenReturn(contactResponse);

        APIGatewayProxyResponseEvent out = LambdaClient.invoke(APIGatewayProxyResponseEvent.class, requestEvent);

        verify(mockAddContactRequestTransformer).apply(addContactRequest);
        verify(mockContactRepository).put(contact);
        verify(mockContactResponseTransformer).apply(contact);
        assertEquals(201, out.getStatusCode());
        ContactResponse response = objectMapper.readValue(out.getBody(), ContactResponse.class);
        assertEquals("Mariano, Lopez", response.getFirstAndLastName());
        assertEquals("ID", response.getId());
        assertEquals("CREATED", response.getStatus());
    }

    @Test
    public void shouldFindByIdWhenGetProxy() throws JsonProcessingException {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(SdkHttpMethod.GET.name())
                .withPath("/proxyzTest/ID")
                .withPathParameters(Map.of("contactId", "ID"));
        Contact contact = Contact.builder().id("ID").firstName("Mariano").lastName("Lopez").status(Status.CREATED).build();
        ContactResponse contactResponse = ContactResponse.builder().id("ID").firstAndLastName("Mariano, Lopez").status("CREATED").build();

        when(mockContactRepository.findById("ID")).thenReturn(contact);
        when(mockContactResponseTransformer.apply(contact)).thenReturn(contactResponse);

        APIGatewayProxyResponseEvent out = LambdaClient.invoke(APIGatewayProxyResponseEvent.class, requestEvent);

        verify(mockContactRepository).findById("ID");
        verify(mockContactResponseTransformer).apply(contact);
        assertEquals(200, out.getStatusCode());
        ContactResponse response = objectMapper.readValue(out.getBody(), ContactResponse.class);
        assertEquals("Mariano, Lopez", response.getFirstAndLastName());
        assertEquals("ID", response.getId());
        assertEquals("CREATED", response.getStatus());
    }

    @Test
    public void shouldGetBadRequestWhenUnsupportedMethodProxy() {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent()
                .withHttpMethod(SdkHttpMethod.DELETE.name())
                .withPath("/");

        APIGatewayProxyResponseEvent out = LambdaClient.invoke(APIGatewayProxyResponseEvent.class, requestEvent);

        Mockito.verifyNoInteractions(mockContactRepository, mockAddContactRequestTransformer, mockContactResponseTransformer);
        assertEquals(400, out.getStatusCode());
        assertEquals("unsupported method/path", out.getBody());
    }*/

}
