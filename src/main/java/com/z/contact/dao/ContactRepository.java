package com.z.contact.dao;

import com.z.contact.domain.Contact;

public interface ContactRepository {
    void put(Contact contact);
    Contact findById(String id);
}
