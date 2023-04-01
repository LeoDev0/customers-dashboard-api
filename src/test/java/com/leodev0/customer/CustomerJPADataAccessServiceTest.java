package com.leodev0.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    private Faker faker;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();

        Mockito.verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {
        int id = 1;

        underTest.selectCustomerById(id);

        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.number().numberBetween(18, 70)
        );

        underTest.insertCustomer(customer);

        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        String email = faker.internet().safeEmailAddress();

        underTest.existsCustomerWithEmail(email);

        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        int id = 1;

        underTest.deleteCustomerById(id);

        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void existsCustomerWithId() {
        int id = 1;

        underTest.existsCustomerWithId(id);

        Mockito.verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                1,
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.number().numberBetween(18, 70)
        );

        underTest.updateCustomer(customer);

        Mockito.verify(customerRepository).save(customer);
    }
}