package com.z.contact.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class JacksonConfig {

    @Produces
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
