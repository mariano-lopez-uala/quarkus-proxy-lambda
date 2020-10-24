package com.z.contact.service;

import com.z.contact.dto.AddContactRequest;
import com.z.contact.dto.ContactResponse;

import java.util.Optional;

public interface ContactService {
    ContactResponse add(AddContactRequest addContactRequest);
    Optional<ContactResponse> findById(String contactId);
}
