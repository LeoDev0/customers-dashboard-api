package com.leodev0.customer;

import com.leodev0.exception.DuplicateResourceException;
import com.leodev0.exception.RequestValidationException;
import com.leodev0.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCostumerById(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        if (customerDao.existsCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("Email already taken");
        }

        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {
        if (!customerDao.existsCustomerWithId(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(id));
        }

        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest updateRequest) {
        Customer customer = getCostumerById(id);
        boolean changes = false;

        if (isValidUpdatableField(updateRequest.name(), customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (isValidUpdatableField(updateRequest.age(), customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if (isValidUpdatableField(updateRequest.email(), customer.getEmail())) {
            if (customerDao.existsCustomerWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException("Email already taken");
            }

            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }

    private <T> boolean isValidUpdatableField(T requestField, T entityField) {
        if (requestField == null || requestField.equals(entityField)) {
            return false;
        }

        return true;

//        return requestField != null && !requestField.equals(entityField); // simplified version
    }
}
