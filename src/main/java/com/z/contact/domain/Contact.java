package com.z.contact.domain;


import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@RegisterForReflection
public class Contact {
    private String id;
    private String firstName;
    private String lastName;
    private Status status;
}