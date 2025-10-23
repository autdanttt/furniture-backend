package org.frogcy.furnitureadmin.dashboard.impl;

import org.frogcy.furnitureadmin.customer.CustomerRepository;
import org.frogcy.furnitureadmin.dashboard.DashboardService;
import org.frogcy.furnitureadmin.dashboard.dto.DashboardSummaryDTO;
import org.frogcy.furnitureadmin.dashboard.dto.GroupByPeriod;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueOrderStatsDTO;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueStatsDTO;
import org.frogcy.furnitureadmin.order.OrderRepository;
import org.frogcy.furnitureadmin.product.ProductRepository;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

    @Override
    public List<RevenueStatsDTO> getRevenueStats(Date startDate, Date endDate, GroupByPeriod groupBy) {
        return orderRepository.getRevenueStats(startDate, endDate, groupBy);
    }

    @Override
    public List<RevenueOrderStatsDTO> getRevenueAndOrders(String range) {
        LocalDate startDate = switch (range.toUpperCase()) {
            case "1M" -> LocalDate.now().minusMonths(1);
            case "6M" -> LocalDate.now().minusMonths(6);
            case "1Y" -> LocalDate.now().minusYears(1);
            default -> LocalDate.of(2000, 1, 1);
        };

        return orderRepository.getRevenueAndOrdersByMonth(startDate);
    }
}
