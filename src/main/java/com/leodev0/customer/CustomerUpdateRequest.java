package com.leodev0.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
