package com.z.contact.dao;

import com.z.contact.domain.Contact;

import java.util.Optional;

public interface ContactRepository {
    void put(Contact contact);
    Optional<Contact> findById(String id);
}
