package org.frogcy.furnitureadmin.dashboard;

import org.frogcy.furnitureadmin.dashboard.dto.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface DashboardService {
    DashboardSummaryDTO getSummary();

//    public List<RevenueStatsDTO> getRevenueStats(Date startDate, Date endDate, GroupByPeriod groupBy);

//    public List<RevenueOrderStatsDTO> getRevenueAndOrders(String range);
     List<StatsDataPoint> getRevenueAndOrderStats(StatsPeriod period);

    public List<StatsDataPoint> getHourlyStatsForDay(LocalDate day);

    List<StatsDataPoint> getStatsForCustomRange(LocalDate startDate, LocalDate endDate);

    public List<CategoryStatsDataPoint> getCategoryStats(StatsPeriod period);

}
