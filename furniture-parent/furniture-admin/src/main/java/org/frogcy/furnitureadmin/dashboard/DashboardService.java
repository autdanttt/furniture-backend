package org.frogcy.furnitureadmin.dashboard;

import org.frogcy.furnitureadmin.dashboard.dto.DashboardSummaryDTO;
import org.frogcy.furnitureadmin.dashboard.dto.GroupByPeriod;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueOrderStatsDTO;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueStatsDTO;

import java.util.Date;
import java.util.List;

public interface DashboardService {
    DashboardSummaryDTO getSummary();

    public List<RevenueStatsDTO> getRevenueStats(Date startDate, Date endDate, GroupByPeriod groupBy);

    public List<RevenueOrderStatsDTO> getRevenueAndOrders(String range);
}
