package com.leodev0.customer;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCostumerById(Integer id) {
        return customerDao.selectCustomerById(id).orElseThrow(() -> new IllegalArgumentException("customer with id [%s] not found".formatted(id)));
    }
}
