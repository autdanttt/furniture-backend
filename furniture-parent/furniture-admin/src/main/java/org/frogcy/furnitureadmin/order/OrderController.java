package org.frogcy.furnitureadmin.order;

import org.frogcy.furnitureadmin.order.dto.OrderResponseDTO;
import org.frogcy.furnitureadmin.order.dto.OrderSummaryDTO;
import org.frogcy.furnitureadmin.order.dto.UpdateOrderStatusRequest;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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


    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
        OrderResponseDTO dto = orderService.get(orderId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER', 'ORDER_MANAGER')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestBody UpdateOrderStatusRequest request){
        orderService.updateStatus(orderId, request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order status updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}
