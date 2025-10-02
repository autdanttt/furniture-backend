package org.frogcy.furniturecustomer.customer.impl;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.customer.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer getByEmail(String email) {
        return customerRepository.getUserByEmail(email);
    }
}
