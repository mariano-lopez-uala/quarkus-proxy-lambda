package com.z.contact.service;

import com.z.contact.dao.ContactRepository;
import com.z.contact.domain.Contact;
import com.z.contact.dto.AddContactRequest;
import com.z.contact.dto.ContactResponse;
import com.z.contact.transformer.AddContactRequestTransformer;
import com.z.contact.transformer.ContactResponseTransformer;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class DynamoContactService implements ContactService {
    private final AddContactRequestTransformer addContactRequestTransformer;
    private final ContactResponseTransformer contactResponseTransformer;
    private final ContactRepository contactRepository;

    public DynamoContactService(AddContactRequestTransformer addContactRequestTransformer,
                                ContactResponseTransformer contactResponseTransformer,
                                ContactRepository contactRepository) {
        this.addContactRequestTransformer = addContactRequestTransformer;
        this.contactResponseTransformer = contactResponseTransformer;
        this.contactRepository = contactRepository;
    }

    @Override
    public ContactResponse add(AddContactRequest addContactRequest) {
        log.info("Add contact request: {}", addContactRequest);
        Contact contact = addContactRequestTransformer.apply(addContactRequest);
        contactRepository.put(contact);
        return contactResponseTransformer.apply(contact);
    }

    @Override
    public Optional<ContactResponse> findById(String contactId) {
        log.info("FindById: {}", contactId);
        Optional<Contact> optionalContact = contactRepository.findById(contactId);
        return optionalContact.map(contactResponseTransformer);
    }
}
