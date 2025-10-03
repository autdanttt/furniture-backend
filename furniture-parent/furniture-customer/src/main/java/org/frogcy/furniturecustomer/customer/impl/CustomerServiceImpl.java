package org.frogcy.furniturecustomer.customer.impl;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecustomer.auth.EmailService;
import org.frogcy.furniturecustomer.auth.dto.CustomerRegisterDTO;
import org.frogcy.furniturecustomer.customer.CustomerAlreadyExistException;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.customer.CustomerService;
import org.frogcy.furniturecustomer.customer.dto.CustomerMapper;
import org.frogcy.furniturecustomer.security.jwt.JwtUtility;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final EmailService emailService;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder, JwtUtility jwtUtility, EmailService emailService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtility = jwtUtility;
        this.emailService = emailService;
    }

    @Override
    public Customer getByEmail(String email) {
        return customerRepository.getUserByEmail(email);
    }

    @Override
    public void registerUser(CustomerRegisterDTO dto) {
        if(customerRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new CustomerAlreadyExistException("Customer already exist with email: " + dto.getEmail());
        }

        Customer customerRequest = customerMapper.customerRegisterDtoToCustomer(dto);



        Role role = new Role(7);
        customerRequest.setEnabled(true);
        customerRequest.setPassword(passwordEncoder.encode(dto.getPassword()));
        customerRequest.setRoles(Set.of(role));

        Customer savedCustomer = customerRepository.save(customerRequest);

        String token = jwtUtility.generateEmailVerificationToken(savedCustomer);

        emailService.sendVerificationEmail(savedCustomer.getEmail(), token);
    }
}
