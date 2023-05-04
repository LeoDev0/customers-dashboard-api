package com.leodev0.customer;

import com.github.javafaker.Faker;
import com.leodev0.customer.enums.Gender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Faker faker = new Faker();

        int id = 10;
        Customer customer = new Customer(
                id,
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.number().numberBetween(18, 70),
                Gender.MALE
        );

        Mockito.when(resultSet.getInt("id")).thenReturn(customer.getId());
        Mockito.when(resultSet.getString("name")).thenReturn(customer.getName());
        Mockito.when(resultSet.getString("email")).thenReturn(customer.getEmail());
        Mockito.when(resultSet.getInt("age")).thenReturn(customer.getAge());
        Mockito.when(resultSet.getString("gender")).thenReturn(customer.getGender().name());

        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        assertThat(actual).isEqualTo(customer);
    }
}