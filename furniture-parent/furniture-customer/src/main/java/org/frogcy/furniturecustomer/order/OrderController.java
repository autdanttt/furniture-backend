package org.frogcy.furniturecustomer.order;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.customer.dto.CustomerNotFoundException;
import org.frogcy.furniturecustomer.order.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
        Customer customer = getCustomer();
        orderService.cancelOrder(customer, id);
        Map<String, String> map = new HashMap<>();
        map.put("message", "Order cancelled");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @GetMapping("/{orderId}/track")
    public ResponseEntity<?> trackOrder(@PathVariable Integer orderId) {
        Customer customer = getCustomer();

        List<OrderTrackResponseDTO> response = orderService.getOrderTracking(orderId, customer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    private Customer getCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("User not found: " + email));
    }

}
