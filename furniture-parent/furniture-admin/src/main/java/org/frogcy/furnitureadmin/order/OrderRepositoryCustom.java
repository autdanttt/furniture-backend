package org.frogcy.furnitureadmin.order;

import org.frogcy.furnitureadmin.dashboard.dto.GroupByPeriod;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueOrderStatsDTO;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueStatsDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderRepositoryCustom {
    List<RevenueStatsDTO> getRevenueStats(Date startDate, Date endDate, GroupByPeriod groupBy);
    List<RevenueOrderStatsDTO> getRevenueAndOrdersByMonth(LocalDate startDate);
}
