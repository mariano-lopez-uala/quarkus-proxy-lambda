package com.z.contact.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddContactRequest {
    private String firstName;
    private String lastName;
}
