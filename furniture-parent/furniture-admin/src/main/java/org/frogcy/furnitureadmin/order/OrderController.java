package org.frogcy.furnitureadmin.order;

import org.frogcy.furnitureadmin.order.dto.OrderSummaryDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword
    ) {
        PageResponseDTO<OrderSummaryDTO> response = orderService.getOrders(page, size, sortField, sortDir, keyword);

        return ResponseEntity.ok(response);
    }


}
