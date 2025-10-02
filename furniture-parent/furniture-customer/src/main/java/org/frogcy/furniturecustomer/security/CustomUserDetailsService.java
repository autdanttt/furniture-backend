package org.frogcy.furniturecustomer.security;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.User;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> findByEmail = customerRepository.findByEmail(username);
        if(!findByEmail.isPresent()) {
            throw new UsernameNotFoundException("No user found with email: " + findByEmail);
        }
        return new CustomUserDetails(findByEmail.get());

    }
}
