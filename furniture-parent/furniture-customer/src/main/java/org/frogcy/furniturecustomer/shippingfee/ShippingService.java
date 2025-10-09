package org.frogcy.furniturecustomer.shippingfee;

import org.frogcy.furniturecommon.entity.Customer;

public interface ShippingService {
    long calculate(Integer provinceCode, Customer customer);
}
