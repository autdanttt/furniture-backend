package org.frogcy.furniturecustomer.order;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.customer.dto.CustomerNotFoundException;
import org.frogcy.furniturecustomer.order.dto.OrderRequestDTO;
import org.frogcy.furniturecustomer.order.dto.OrderResponseDTO;
import org.frogcy.furniturecustomer.order.dto.OrderResultDTO;
import org.frogcy.furniturecustomer.order.dto.OrderSummaryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    private final OrderService orderService;
    private final CustomerRepository customerRepository;

    public OrderController(OrderService orderService, CustomerRepository customerRepository) {
        this.orderService = orderService;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderRequestDTO dto) {
        Customer customer = getCustomer();
        OrderResultDTO response = orderService.create(customer, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summaryOrders() {
        Customer customer = getCustomer();
        OrderSummaryDTO summaryDTO = orderService.summaryOrder(customer);
        return new ResponseEntity<>(summaryDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Integer id) {
        Customer customer = getCustomer();

        OrderResponseDTO dto = orderService.get(customer,id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getOrders() {
        Customer customer = getCustomer();
        List<OrderResponseDTO> list = orderService.getAll(customer);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }



    private Customer getCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("User not found: " + email));
    }

}
