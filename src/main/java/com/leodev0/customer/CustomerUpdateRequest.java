package com.leodev0.customer;

import com.leodev0.customer.enums.Gender;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age,
        Gender gender
) {
}
