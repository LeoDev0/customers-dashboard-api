package com.leodev0.customer;

import com.leodev0.AbstractTestcontainers;
import com.leodev0.customer.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(18, 70),
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        List<Customer> actual = underTest.selectAllCustomers();

        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(18, 70),
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
//        assertThat(actual).usingRecursiveComparison().isEqualTo(customer);
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        var invalidId = -1;

        Optional<Customer> actual = underTest.selectCustomerById(invalidId);

        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(18, 70),
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> findSavedCustomer = underTest.selectCustomerById(customerId);

        assertThat(findSavedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void existsCustomerWithEmail() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(18, 70),
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        boolean actual = underTest.existsCustomerWithEmail(email);

        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithEmailReturnsFalseWhenDoesNotExists() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        boolean actual = underTest.existsCustomerWithEmail(email);

        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(18, 70),
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomerById(customerId);

        boolean customerExists = underTest.existsCustomerWithId(customerId);

        assertThat(customerExists).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(18, 70),
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean actual = underTest.existsCustomerWithId(customerId);

        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdReturnsFalseWhenIdNotPresent() {
        var id = -1;

        boolean actual = underTest.existsCustomerWithId(id);

        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(18, 70),
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        var customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updateCustomerObject = new Customer(
                customerId,
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(18, 70),
                Gender.FEMALE
        );

        underTest.updateCustomer(updateCustomerObject);

        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(updateCustomerObject.getName());
            assertThat(c.getEmail()).isEqualTo(updateCustomerObject.getEmail());
            assertThat(c.getAge()).isEqualTo(updateCustomerObject.getAge());
            assertThat(c.getGender()).isEqualTo(updateCustomerObject.getGender());
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20,
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When update without no changes
        Customer update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });
    }
}