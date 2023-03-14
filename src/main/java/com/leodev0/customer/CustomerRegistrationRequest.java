package com.leodev0.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) { }
