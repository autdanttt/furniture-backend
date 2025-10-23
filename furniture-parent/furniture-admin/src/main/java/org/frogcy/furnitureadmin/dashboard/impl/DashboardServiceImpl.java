package org.frogcy.furnitureadmin.dashboard.impl;

import org.frogcy.furnitureadmin.customer.CustomerRepository;
import org.frogcy.furnitureadmin.dashboard.DashboardService;
import org.frogcy.furnitureadmin.dashboard.dto.DashboardSummaryDTO;
import org.frogcy.furnitureadmin.order.OrderRepository;
import org.frogcy.furnitureadmin.product.ProductRepository;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public DashboardServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public DashboardSummaryDTO getSummary() {

        long totalOrders = orderRepository.count();
        long totalRevenue = orderRepository.sumTotalByStatus(OrderStatus.DELIVERED);
        long totalCustomers = customerRepository.countByDeletedFalse();
        long totalProducts = productRepository.countByDeletedFalse();
        long shippingOrders = orderRepository.countByStatus(OrderStatus.SHIPPING);

        return new DashboardSummaryDTO(
                totalOrders,
                totalRevenue,
                totalCustomers,
                totalProducts,
                shippingOrders
        );
    }
}
