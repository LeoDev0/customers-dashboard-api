package com.leodev0.customer;

import com.leodev0.exception.DuplicateResourceException;
import com.leodev0.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
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
        if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
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
        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(id));
        }

        customerDao.deleteCustomerById(id);
    }
}
