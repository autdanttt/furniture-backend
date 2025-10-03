package org.frogcy.furniturecustomer.customer;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.auth.dto.CustomerRegisterDTO;

public interface CustomerService {
    Customer getByEmail(String email);

    void registerUser(@Valid CustomerRegisterDTO dto);
}
