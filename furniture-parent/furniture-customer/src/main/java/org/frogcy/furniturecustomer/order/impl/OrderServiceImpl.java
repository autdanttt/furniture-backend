package org.frogcy.furniturecustomer.order.impl;

import jakarta.transaction.Transactional;
import org.frogcy.furniturecommon.entity.CartItem;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderDetail;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.frogcy.furniturecommon.entity.order.OrderTrack;
import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecustomer.cart.CartItemRepository;
import org.frogcy.furniturecustomer.inventory.InventoryRepository;
import org.frogcy.furniturecustomer.order.OrderDetailRepository;
import org.frogcy.furniturecustomer.order.OrderRepository;
import org.frogcy.furniturecustomer.order.OrderService;
import org.frogcy.furniturecustomer.order.dto.OrderRequestDTO;
import org.frogcy.furniturecustomer.order.dto.OrderResultDTO;
import org.frogcy.furniturecustomer.order.dto.OrderSummaryDTO;
import org.frogcy.furniturecustomer.product.ProductRepository;
import org.frogcy.furniturecustomer.shippingfee.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ShippingService shippingService;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CartItemRepository cartItemRepository, OrderDetailRepository orderDetailRepository, ShippingService shippingService, ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.shippingService = shippingService;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
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
}
