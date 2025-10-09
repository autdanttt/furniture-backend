package org.frogcy.furnitureadmin.order.impl;

import jakarta.transaction.Transactional;
import org.frogcy.furnitureadmin.order.OrderRepository;
import org.frogcy.furnitureadmin.order.OrderService;
import org.frogcy.furnitureadmin.order.OrderTrackRepository;
import org.frogcy.furnitureadmin.order.dto.*;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.order.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderTrackRepository orderTrackRepository;
    private final OrderTrackMapper orderTrackMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, OrderTrackRepository orderTrackRepository, OrderTrackMapper orderTrackMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderTrackRepository = orderTrackRepository;
        this.orderTrackMapper = orderTrackMapper;
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

    @Transactional
    @Override
    public void updateStatus(Integer orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + orderId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isShipper = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SHIPPER"));

        OrderStatus newStatus = request.getStatus();

        // ✅ Nếu là shipper → chỉ cho phép cập nhật PICKED, SHIPPING, DELIVERED
        if (isShipper) {
            if (!(newStatus == OrderStatus.PICKED ||
                    newStatus == OrderStatus.SHIPPING ||
                    newStatus == OrderStatus.DELIVERED)) {
                throw new AccessDeniedException("Shipper cannot update to this status");
            }
        }
        // ✅ Nếu là Admin → có thể cập nhật bất kỳ trạng thái nào
        order.setStatus(newStatus);

        // ✅ Nếu giao thành công và là COD → cập nhật trạng thái thanh toán
        if (newStatus == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }


        // Tạo lịch sử trạng thái (OrderTrack)
        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus(request.getStatus());
        track.setNotes(
                request.getNotes() != null ? request.getNotes() : newStatus.defaultDescription()
        );
        track.setUpdatedTime(new Date());
        orderTrackRepository.save(track);

        orderRepository.save(order);
    }

    @Override
    public OrderResponseDTO get(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));

        return getOrderResponseDTO(order);
    }

    private static OrderResponseDTO getOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setFirstName(order.getFirstName());
        dto.setLastName(order.getLastName());
        dto.setEmail(order.getEmail());
        dto.setProvinceName(order.getProvinceName());
        dto.setWardName(order.getWardName());
        dto.setAddressLine(order.getAddressLine());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setShippingCost(order.getShippingCost());
        dto.setProductCost(order.getProductCost());
        dto.setSubtotal(order.getSubtotal());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setTotal(order.getTotal());
        dto.setOrderTime(order.getOrderTime());
        dto.setDeliverDays(order.getDeliverDays());
        dto.setDeliverDate(order.getDeliverDate());

        Set<OrderDetailDTO> details = getOrderDetailDTOS(order);
        List<OrderTrackDTO> orderTracks = getOrderTrackDTOS(order);

        dto.setDetails(details);
        dto.setOrderTracks(orderTracks);
        return dto;
    }

    private static List<OrderTrackDTO> getOrderTrackDTOS(Order order) {
        return order.getOrderTracks().stream()
                .map(orderTrack ->{
                        OrderTrackDTO dto = new OrderTrackDTO();
                        dto.setId(orderTrack.getId());
                        dto.setStatus(orderTrack.getStatus());
                        dto.setNotes(orderTrack.getNotes());
                        dto.setUpdatedTime(orderTrack.getUpdatedTime());
                        return dto;
                }).toList();
    }

    private static Set<OrderDetailDTO> getOrderDetailDTOS(Order order) {
        Set<OrderDetailDTO> details = new HashSet<>();

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            OrderDetailDTO detailDTO = new OrderDetailDTO();
            detailDTO.setProductId(orderDetail.getProduct().getId());
            detailDTO.setProductName(orderDetail.getProduct().getName());
            detailDTO.setQuantity(orderDetail.getQuantity());
            detailDTO.setImageUrl(orderDetail.getProduct().getMainImage().getImageUrl());
            detailDTO.setPrice(orderDetail.getUnitPrice());

            details.add(detailDTO);
        }
        return details;
    }
}
