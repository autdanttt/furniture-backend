package org.frogcy.furnitureadmin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {
    private long totalOrders;
    private long totalRevenue;
    private long totalCustomers;
    private long totalProducts;
    private long shippingOrders;
}
