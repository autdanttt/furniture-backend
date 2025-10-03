package org.frogcy.furniturecustomer.customer;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.customer.dto.CustomerNotFoundException;
import org.frogcy.furniturecustomer.customer.dto.CustomerResponseDTO;
import org.frogcy.furniturecustomer.customer.dto.CustomerUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @PutMapping(value = "/information", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateInfo(@RequestPart("customer") @Valid CustomerUpdateDTO dto,
                                        @RequestPart("avatar") @Nullable MultipartFile avatar){


        Customer customer = getCustomer();

        if(!dto.getId().equals(customer.getId())) {
             throw new AccessDeniedException("Access denied");
         }

        CustomerResponseDTO response = customerService.updateInformation(dto, avatar);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Customer getCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email)
               .orElseThrow(() -> new CustomerNotFoundException("User not found: " + email));
        return customer;
    }


}
