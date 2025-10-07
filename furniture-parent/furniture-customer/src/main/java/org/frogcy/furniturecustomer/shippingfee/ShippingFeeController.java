package org.frogcy.furniturecustomer.shippingfee;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.customer.dto.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping")
public class ShippingFeeController {
    private final ShippingService shippingService;
    private final CustomerRepository customerRepository;

    public ShippingFeeController(ShippingService shippingService, CustomerRepository customerRepository) {
        this.shippingService = shippingService;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/fee")
    public ResponseEntity<?> calculateShippingFee(@RequestParam("provinceCode")Integer provinceCode) {
        Customer customer = getCustomer();
        long fee = shippingService.calculate(provinceCode, customer);
        Map<String, Long> map = new HashMap<>();
        map.put("fee", fee);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    private Customer getCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("User not found: " + email));
    }

}
