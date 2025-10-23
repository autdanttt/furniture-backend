package org.frogcy.furnitureadmin.dashboard;

import org.frogcy.furnitureadmin.dashboard.dto.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORDER_MANAGER')")
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        DashboardSummaryDTO response = dashboardService.getSummary();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * API 1: Lấy thống kê theo các kỳ được định sẵn (tuần này, tháng trước, năm nay...).
     * @param period (tùy chọn, mặc định là LAST_YEAR)
     * @return Danh sách các điểm dữ liệu thống kê.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORDER_MANAGER')")
    @GetMapping("/stats")
    public ResponseEntity<List<StatsDataPoint>> getStats(
            @RequestParam(name = "period", defaultValue = "LAST_YEAR") StatsPeriod period) {
        List<StatsDataPoint> stats = dashboardService.getRevenueAndOrderStats(period);
        return ResponseEntity.ok(stats);
    }

    /**
     * API 2: Lấy thống kê theo giờ trong một ngày cụ thể.
     * @param date (bắt buộc), định dạng "YYYY-MM-DD"
     * @return Danh sách 24 điểm dữ liệu thống kê cho 24 giờ.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORDER_MANAGER')")
    @GetMapping("/stats/by-day")
    public ResponseEntity<List<StatsDataPoint>> getHourlyStats(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<StatsDataPoint> stats = dashboardService.getHourlyStatsForDay(date);
        return ResponseEntity.ok(stats);
    }
    /**
     * API 3: Lấy thống kê trong một khoảng thời gian tùy chỉnh.
     * @param startDate (bắt buộc), định dạng "YYYY-MM-DD"
     * @param endDate (bắt buộc), định dạng "YYYY-MM-DD"
     * @return Danh sách các điểm dữ liệu thống kê, được nhóm tự động theo ngày hoặc tháng.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORDER_MANAGER')")
    @GetMapping("/stats/custom-range")
    public ResponseEntity<List<StatsDataPoint>> getStatsForCustomRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<StatsDataPoint> stats = dashboardService.getStatsForCustomRange(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * API: Lấy thống kê đơn hàng theo danh mục.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    @GetMapping("/stats/by-category")
    public ResponseEntity<List<CategoryStatsDataPoint>> getCategoryStats(
            @RequestParam(name = "period", defaultValue = "THIS_YEAR") StatsPeriod period) {
        List<CategoryStatsDataPoint> stats = dashboardService.getCategoryStats(period);
        return ResponseEntity.ok(stats);
    }

    /**
     * API: Lấy thống kê số lượng đơn hàng theo từng trạng thái.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORDER_MANAGER')")
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<OrderStatusStatsDataPoint>> getOrderStatusStats(
            @RequestParam(name = "period", defaultValue = "THIS_MONTH") StatsPeriod period) {
        List<OrderStatusStatsDataPoint> stats = dashboardService.getOrderStatusStats(period);
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'ORDER_MANAGER')")
    @GetMapping("/best-selling-products")
    public ResponseEntity<List<BestSellingProductDataPoint>> getBestSellingProducts(
            @RequestParam(name = "period", defaultValue = "THIS_MONTH") StatsPeriod period) {

        List<BestSellingProductDataPoint> products = dashboardService.getBestSellingProducts(period);
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/new-customers")
    public ResponseEntity<List<NewCustomerDataPoint>> getNewCustomers(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {

        List<NewCustomerDataPoint> customers = dashboardService.getNewCustomers(limit);
        return ResponseEntity.ok(customers);
    }

}
