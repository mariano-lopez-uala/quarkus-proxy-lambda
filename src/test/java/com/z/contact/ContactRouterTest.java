package com.z.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.contact.dao.ContactRepository;
import com.z.contact.domain.Contact;
import com.z.contact.domain.Status;
import com.z.contact.dto.AddContactRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.http.HttpStatusCode;

import javax.inject.Inject;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ContactRouterTest {
    private final String BASE_PATH          = "/proxyzTest";

    private final String CONTACT_ID         = "ce64840a-bc84-4184-b20d-c8a28b5bd812";
    private final String CONTACT_FIRST_NAME = "Mariano";
    private final String CONTACT_LAST_NAME  = "Lopez";
    private final String CONTACT_STATUS     = "CREATED";

    @Inject
    ObjectMapper objectMapper;

    @InjectMock
    ContactRepository mockContactRepository;

    @Test
    public void shouldAddContactWithHttpPost() throws Exception {
        doNothing().when(mockContactRepository).put(any(Contact.class));
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(objectMapper.writeValueAsBytes(new AddContactRequest(CONTACT_FIRST_NAME, CONTACT_LAST_NAME)))
                .when()
                    .post(BASE_PATH)
                .then()
                    .statusCode(HttpStatusCode.CREATED)
                    .contentType(ContentType.JSON)
                    .body("id", notNullValue())
                    .body("firstAndLastName", equalTo(CONTACT_FIRST_NAME.concat(", ").concat(CONTACT_LAST_NAME)))
                    .body("status", equalTo(CONTACT_STATUS));
        verify(mockContactRepository).put(any(Contact.class));
    }

    @Test
    public void shouldFindByIdWithHttpGet() {
        when(mockContactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(dummyContact()));
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get(BASE_PATH.concat("/").concat(CONTACT_ID))
                .then()
                    .statusCode(HttpStatusCode.OK)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(CONTACT_ID))
                    .body("firstAndLastName", equalTo(CONTACT_FIRST_NAME.concat(", ").concat(CONTACT_LAST_NAME)))
                    .body("status", equalTo(CONTACT_STATUS));
        verify(mockContactRepository).findById(CONTACT_ID);
    }

    private Contact dummyContact() {
        return Contact.builder()
                .id(CONTACT_ID)
                .firstName(CONTACT_FIRST_NAME)
                .lastName(CONTACT_LAST_NAME)
                .status(Status.valueOf(CONTACT_STATUS))
                .build();
    }
}
