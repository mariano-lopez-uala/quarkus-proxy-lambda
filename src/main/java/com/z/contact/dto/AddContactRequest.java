package com.z.contact.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@RegisterForReflection
public class AddContactRequest {
    private String firstName;
    private String lastName;
}
