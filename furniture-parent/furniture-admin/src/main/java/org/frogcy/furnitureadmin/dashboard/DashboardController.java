package org.frogcy.furnitureadmin.dashboard;

import org.frogcy.furnitureadmin.dashboard.dto.DashboardSummaryDTO;
import org.frogcy.furnitureadmin.dashboard.dto.GroupByPeriod;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueStatsDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        DashboardSummaryDTO response = dashboardService.getSummary();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/revenue")
//    public ResponseEntity<List<RevenueStatsDTO>> getRevenueStats(
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
//            @RequestParam(defaultValue = "DAY") GroupByPeriod groupBy
//    ) {
//        return ResponseEntity.ok(dashboardService.getRevenueStats(startDate, endDate, groupBy));
//    }

    @GetMapping("/overview-chart")
    public ResponseEntity<?> getOverviewChart(
            @RequestParam(defaultValue = "1M") String range
    ) {
        return ResponseEntity.ok(dashboardService.getRevenueAndOrders(range));
    }


}
