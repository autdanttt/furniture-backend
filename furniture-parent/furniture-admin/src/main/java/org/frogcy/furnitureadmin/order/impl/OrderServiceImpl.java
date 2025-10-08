package org.frogcy.furnitureadmin.order.impl;

import org.frogcy.furnitureadmin.order.OrderRepository;
import org.frogcy.furnitureadmin.order.OrderService;
import org.frogcy.furnitureadmin.order.dto.OrderMapper;
import org.frogcy.furnitureadmin.order.dto.OrderSummaryDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public PageResponseDTO<OrderSummaryDTO> getOrders(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();


        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.search(keyword, pageable);
        List<OrderSummaryDTO> orders = orderPage.getContent().stream()
                .map(order -> {
                    OrderSummaryDTO orderSummaryDTO = orderMapper.toSummaryDTO(order);
                    orderSummaryDTO.setStatus(order.getStatus());
                    return orderSummaryDTO;
                }).toList();

        return new PageResponseDTO<>(
                orders,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }
}
