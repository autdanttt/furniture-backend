package org.frogcy.furnitureadmin.customer;

import org.frogcy.furnitureadmin.customer.dto.CustomerResponseDTO;
import org.frogcy.furnitureadmin.user.UserNotFoundException;
import org.frogcy.furnitureadmin.user.UserRepository;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final UserRepository userRepository;

    public CustomerController(CustomerService customerService, UserRepository userRepository) {
        this.customerService = customerService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORDER_MANAGER','ASSISTANT')")
    @GetMapping
    public ResponseEntity<?> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword
    ) {
        PageResponseDTO<CustomerResponseDTO> response = customerService.getCustomers(page, size, sortField, sortDir, keyword);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT')")
    @PatchMapping("/{customerId}/enabled")
    public ResponseEntity<?> changeEnabled(@PathVariable("customerId") Integer customerId, @RequestParam boolean enabled){
        customerService.changeEnabled(customerId, enabled);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT')")
    @DeleteMapping("/{customerId}")
    public  ResponseEntity<?> delete(@PathVariable("customerId") Integer customerId){
        User user = getUser();
        customerService.deleteCustomer(user.getId(), customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private User getUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }
}
