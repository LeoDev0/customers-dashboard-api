package com.leodev0.customer;

import com.github.javafaker.Faker;
import com.leodev0.exception.DuplicateResourceException;
import com.leodev0.exception.RequestValidationException;
import com.leodev0.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    private Faker faker;

    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();

        Mockito.verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCostumerById() {
        int id = 10;

        Customer customer = new Customer(
                id,
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.number().numberBetween(18, 70)
        );

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Customer actual = underTest.getCostumerById(id);

        Mockito.verify(customerDao).selectCustomerById(id);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerByIdReturnsEmptyOptional() {
        int id = 10;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCostumerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = faker.internet().safeEmailAddress();

        Mockito.when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                faker.name().fullName(),
                email,
                faker.number().numberBetween(18, 70)
        );

        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailIsAlreadyTakenWhileAddingNewCustomer() {
        String email = faker.internet().safeEmailAddress();

        Mockito.when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                faker.name().fullName(),
                email,
                faker.number().numberBetween(18, 70)
        );


        Mockito.verify(customerDao, never()).insertCustomer(any());
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already taken");
    }

    @Test
    void deleteCustomerById() {
        int id = 10;

        Mockito.when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        underTest.deleteCustomerById(id);

        Mockito.verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenIdDoesNotExistsWhileDeletingCustomer() {
        int id = 10;

        Mockito.when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        Mockito.verify(customerDao, never()).deleteCustomerById(any());
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id [%s] not found".formatted(id));
    }

    @Test
    void updateCustomer() {
        int id = 10;
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.number().numberBetween(18, 70);

        Customer customer = new Customer(
                id,
                name,
                email,
                age
        );

        String newEmail = faker.internet().safeEmailAddress();
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                faker.name().fullName(),
                newEmail,
                faker.number().numberBetween(18, 70)
        );

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer).usingRecursiveComparison().isEqualTo(customer);
    }

    @Test
    void canUpdateOnlyOneCustomerField() {
        int id = 10;
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.number().numberBetween(18, 70);

        Customer customer = new Customer(
                id,
                name,
                email,
                age
        );

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                faker.name().fullName(),
                null,
                null
        );

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void willThrowWhenEmailAlreadyExistsWhileUpdatingCustomer() {
        int id = 10;
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.number().numberBetween(18, 70);

        Customer customer = new Customer(
                id,
                name,
                email,
                age
        );

        String newEmail = faker.internet().safeEmailAddress();
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customer.getName(),
                newEmail,
                customer.getAge()
        );

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        verify(customerDao, never()).updateCustomer(any());
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already taken");
    }

    @Test
    void willThrowWhenThereIsNoDataToUpdate() {
        int id = 10;
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.number().numberBetween(18, 70);

        Customer customer = new Customer(
                id,
                name,
                email,
                age
        );

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        verify(customerDao, never()).updateCustomer(any());
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("No data changes found");
    }
}