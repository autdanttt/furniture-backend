package org.frogcy.furniturecustomer.order.impl;

import jakarta.transaction.Transactional;
import org.frogcy.furniturecommon.entity.CartItem;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.order.*;
import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecustomer.cart.CartItemRepository;
import org.frogcy.furniturecustomer.inventory.InventoryRepository;
import org.frogcy.furniturecustomer.order.*;
import org.frogcy.furniturecustomer.order.dto.*;
import org.frogcy.furniturecustomer.product.ProductRepository;
import org.frogcy.furniturecustomer.shippingfee.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ShippingService shippingService;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderTrackRepository orderTrackRepository;
    private final OrderTrackMapper orderTrackMapper;

    public OrderServiceImpl(OrderRepository orderRepository, CartItemRepository cartItemRepository, OrderDetailRepository orderDetailRepository, ShippingService shippingService, ProductRepository productRepository, InventoryRepository inventoryRepository, OrderTrackRepository orderTrackRepository, OrderTrackMapper orderTrackMapper) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.shippingService = shippingService;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderTrackRepository = orderTrackRepository;
        this.orderTrackMapper = orderTrackMapper;
    }

    @Transactional
    @Override
    public OrderResultDTO create(Customer customer, OrderRequestDTO dto) {
        Order order = new Order();
        order.setFirstName(dto.getFirstName());
        order.setLastName(dto.getLastName());

        order.setProvinceName(dto.getProvinceName());
        order.setWardName(dto.getWardName());
        order.setAddressLine(dto.getAddressLine());
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setEmail(dto.getEmail());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.NEW);
        Date now = new Date();
        order.setOrderTime(now);


        List<CartItem> items = cartItemRepository.findByCustomer(customer);
        long subTotal = 0;
        long totalProductCost = 0;
        Set<OrderDetail> orderDetails = new HashSet<>();
        for (CartItem item : items) {
            Product product = item.getProduct();
            Inventory inventoryProduct = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new IllegalStateException("No inventory record for product " + product.getName()));

            if(inventoryProduct.getQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Product " + product.getName() + " is out of stock");
            }

            long productCost = item.getQuantity() * product.getFinalPrice();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setProductCost(productCost);
            orderDetail.setUnitPrice(product.getFinalPrice());
            orderDetail.setSubtotal(productCost + 0L);
            orderDetail.setOrder(order);

            orderDetails.add(orderDetail);
            subTotal += orderDetail.getSubtotal();
            totalProductCost += productCost;
            inventoryProduct.setQuantity(inventoryProduct.getQuantity() - item.getQuantity());
            inventoryRepository.save(inventoryProduct);
        }

        long shippingCost = shippingService.calculate(dto.getProvinceCode(), customer);
        if(shippingCost != dto.getEstimatedShippingFee()){
            throw new IllegalArgumentException("Shipping fee is not equal to estimated shipping fee.");
        }
        cartItemRepository.deleteAll(items);
        subTotal = subTotal + shippingCost;

        order.setOrderDetails(orderDetails);
        order.setShippingCost(shippingCost);
        order.setProductCost(totalProductCost);
        order.setSubtotal(subTotal);
        order.setTotal(subTotal + 0L);


        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setOrder(order);
        orderTrack.setStatus(OrderStatus.NEW);
        orderTrack.setNotes(OrderStatus.NEW.defaultDescription());
        orderTrack.setUpdatedTime(now);

        order.getOrderTracks().add(orderTrack);

        order = orderRepository.save(order);

        OrderResultDTO summaryDTO = new OrderResultDTO();
        summaryDTO.setOrderId(order.getId());
        summaryDTO.setOrderStatus(order.getStatus());
        summaryDTO.setTotal(order.getTotal());
        summaryDTO.setOrderTime(order.getOrderTime());


        log.info("Created order #{} for customer {} with total {}", order.getId(), customer.getEmail(), order.getTotal());
        return summaryDTO;
    }

    @Override
    public OrderSummaryDTO summaryOrder(Customer customer) {
        List<CartItem> items = cartItemRepository.findByCustomer(customer);
        long subTotal = 0;
        int countProduct = 0;
        for (CartItem item : items) {
            Product product = item.getProduct();
            Inventory inventoryProduct = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new IllegalStateException("No inventory record for product " + product.getName()));

            if(inventoryProduct.getQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Product " + product.getName() + " is out of stock");
            }
            long productCost = item.getQuantity() * product.getFinalPrice();
            countProduct += item.getQuantity();

            subTotal += productCost;
        }

        OrderSummaryDTO summaryDTO = new OrderSummaryDTO();
        summaryDTO.setSubTotal(subTotal);
        summaryDTO.setItems(countProduct);
        return summaryDTO;
    }

    @Override
    public OrderResponseDTO get(Customer customer, Integer id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalStateException("No order found for id " + id));

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

        dto.setDetails(details);
        return dto;
    }

    @Override
    public List<OrderResponseDTO> getAll(Customer customer) {
        List<Order> orderList = orderRepository.findAllByCustomerOrderByOrderTimeDesc(customer);
        List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderResponseDTO responseDTO = getOrderResponseDTO(order);
            orderResponseDTOList.add(responseDTO);
        }
        return orderResponseDTOList;
    }

    @Override
    public void cancelOrder(Customer customer, Integer id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("No order found for id " + id));
        if(!order.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("You are not allowed to cancel this order");
        }

        if(order.getStatus() != OrderStatus.NEW && order.getStatus() != OrderStatus.PROCESSING){
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }

        Date now = new Date();
        if(order.getPaymentMethod() == PaymentMethod.COD){
            order.setStatus(OrderStatus.CANCELLED);
            OrderTrack orderTrack = new OrderTrack();
            orderTrack.setOrder(order);
            orderTrack.setStatus(OrderStatus.CANCELLED);
            orderTrack.setNotes("Customer " + customer.getEmail() + " has been cancelled");
            orderTrack.setUpdatedTime(now);
            orderTrackRepository.save(orderTrack);
        }

        // ✅ Nếu thanh toán Online và đã trả tiền → tạo refund
        else if (order.getPaymentMethod() == PaymentMethod.PAYPAL
                && order.getPaymentStatus() == PaymentStatus.PAID) {

            order.setPaymentStatus(PaymentStatus.REFUNDING);
            OrderTrack orderTrack = new OrderTrack();
            orderTrack.setOrder(order);
            orderTrack.setStatus(order.getStatus());
            orderTrack.setNotes("Refund process started");
            orderTrack.setUpdatedTime(now);
            orderTrackRepository.save(orderTrack);

            try {
                // Giả lập gọi đến payment gateway (VD: VNPay, PayOS, MoMo)
//                paymentGatewayService.refund(order);
                order.setPaymentStatus(PaymentStatus.REFUNDED);
                order.setStatus(OrderStatus.REFUNDED);

                OrderTrack orderTrack2 = new OrderTrack();
                orderTrack2.setOrder(order);
                orderTrack2.setStatus(OrderStatus.REFUNDED);
                orderTrack2.setNotes("Customer refunded successfully");
                orderTrack2.setUpdatedTime(now);

                orderTrackRepository.save(orderTrack2);
            } catch (Exception e) {
                order.setPaymentStatus(PaymentStatus.FAILED);
                throw new IllegalStateException("Refund failed: " + e.getMessage());
            }
        }
        // ✅ Hoàn lại hàng tồn kho nếu đã trừ
        for (OrderDetail detail : order.getOrderDetails()) {
            Inventory inv = inventoryRepository.findByProduct(detail.getProduct())
                    .orElseThrow(() -> new IllegalStateException("Inventory not found for product"));
            inv.setQuantity(inv.getQuantity() + detail.getQuantity());
        }

    }

    @Override
    public List<OrderTrackResponseDTO> getOrderTracking(Integer orderId, Customer customer) {
        if(orderRepository.findById(orderId).isEmpty()) {
            throw new OrderNotFoundException("No order found for id " + orderId);
        }
        List<OrderTrack> orderTracks = orderTrackRepository.findByOrderIdOrderByUpdatedTimeDesc(orderId);


        List<OrderTrackResponseDTO> responseDTOS = new ArrayList<>();
        for (OrderTrack ot : orderTracks) {
            OrderTrackResponseDTO dto = orderTrackMapper.toDto(ot);
            responseDTOS.add(dto);
        }
        return responseDTOS;
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
