package org.frogcy.furniturecustomer.customer;

import org.frogcy.furniturecommon.entity.Customer;

public interface CustomerService {
    Customer getByEmail(String email);
}
