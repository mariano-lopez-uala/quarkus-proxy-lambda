package com.z.contact.transformer;

import com.z.contact.domain.Contact;
import com.z.contact.dto.ContactResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.function.Function;

@ApplicationScoped
public class ContactResponseTransformer implements Function<Contact, ContactResponse> {
    @Override
    public ContactResponse apply(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstAndLastName(contact.getFirstName().concat(", ").concat(contact.getLastName()))
                .status(contact.getStatus().name())
                .build();
    }
}
