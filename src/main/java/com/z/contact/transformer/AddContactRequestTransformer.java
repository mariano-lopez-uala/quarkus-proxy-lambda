package com.z.contact.transformer;

import com.z.contact.domain.Contact;
import com.z.contact.domain.Status;
import com.z.contact.dto.AddContactRequest;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import java.util.function.Function;

@ApplicationScoped
public class AddContactRequestTransformer implements Function<AddContactRequest, Contact> {

    @Override
    public Contact apply(AddContactRequest addContactRequest) {
        return Contact.builder()
                .id(UUID.randomUUID().toString())
                .firstName(addContactRequest.getFirstName())
                .lastName(addContactRequest.getLastName())
                .status(Status.CREATED)
                .build();
    }
}
