package com.z.contact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dao.ContactRepository;
import com.z.contact.domain.Contact;
import com.z.contact.domain.Status;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.dto.ContactResponse;
import com.z.contact.transformer.AddContactRequestTransformer;
import com.z.contact.transformer.ContactResponseTransformer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.http.HttpStatusCode;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ContactRouterTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMock
    ContactRepository mockContactRepository;
    @InjectMock
    AddContactRequestTransformer mockAddContactRequestTransformer;
    @InjectMock
    ContactResponseTransformer mockContactResponseTransformer;

    @Test
    public void shouldAddContactWhenPostProxy() throws JsonProcessingException {
        AddContactRequest addContactRequest = new AddContactRequest("Mariano", "Lopez");
        Contact contact = Contact.builder().id("ID").firstName("Mariano").lastName("Lopez").status(Status.CREATED).build();
        ContactResponse contactResponse = ContactResponse.builder().id("ID").firstAndLastName("Mariano, Lopez").status("CREATED").build();

        when(mockAddContactRequestTransformer.apply(addContactRequest)).thenReturn(contact);
        doNothing().when(mockContactRepository).put(contact);
        when(mockContactResponseTransformer.apply(contact)).thenReturn(contactResponse);
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(objectMapper.writeValueAsBytes(addContactRequest))
                .when()
                    .post("/proxyzTest")
                .then()
                    .statusCode(HttpStatusCode.CREATED)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(contactResponse.getId()))
                    .body("firstAndLastName", equalTo(contactResponse.getFirstAndLastName()))
                    .body("status", equalTo(contactResponse.getStatus()));

        verify(mockAddContactRequestTransformer).apply(addContactRequest);
        verify(mockContactRepository).put(contact);
        verify(mockContactResponseTransformer).apply(contact);
    }

    @Test
    public void shouldFindByIdWhenGetProxy() {
        Contact contact = Contact.builder().id("ID").firstName("Mariano").lastName("Lopez").status(Status.CREATED).build();
        ContactResponse contactResponse = ContactResponse.builder().id("ID").firstAndLastName("Mariano, Lopez").status("CREATED").build();

        when(mockContactRepository.findById("ID")).thenReturn(contact);
        when(mockContactResponseTransformer.apply(contact)).thenReturn(contactResponse);
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/proxyzTest/ID")
                .then()
                    .statusCode(HttpStatusCode.OK)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(contactResponse.getId()))
                    .body("firstAndLastName", equalTo(contactResponse.getFirstAndLastName()))
                    .body("status", equalTo(contactResponse.getStatus()));

        verify(mockContactRepository).findById("ID");
        verify(mockContactResponseTransformer).apply(contact);
    }
}
