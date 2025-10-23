package org.frogcy.furnitureadmin.dashboard.impl;

import org.frogcy.furnitureadmin.customer.CustomerRepository;
import org.frogcy.furnitureadmin.dashboard.DashboardService;
import org.frogcy.furnitureadmin.dashboard.dto.*;
import org.frogcy.furnitureadmin.order.OrderRepository;
import org.frogcy.furnitureadmin.product.ProductRepository;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<StatsDataPoint> getRevenueAndOrderStats(StatsPeriod period) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today;

        // Mặc định nhóm theo ngày, sẽ thay đổi cho các kỳ dài
        boolean groupByDay = true;

        switch (period) {
            // --- TUẦN ---
            case THIS_WEEK:
                startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                break;
            case LAST_WEEK:
                startDate = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = startDate.plusDays(6);
                break;

            // --- THÁNG ---
            case THIS_MONTH:
                startDate = today.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case LAST_MONTH:
                LocalDate lastMonthDate = today.minusMonths(1);
                startDate = lastMonthDate.with(TemporalAdjusters.firstDayOfMonth());
                endDate = lastMonthDate.with(TemporalAdjusters.lastDayOfMonth());
                break;

            // --- QUÝ ---
            case THIS_QUARTER:
                startDate = today.with(IsoFields.DAY_OF_QUARTER, 1);
                groupByDay = false; // Quý thì nên nhóm theo tháng
                break;
            case LAST_QUARTER:
                LocalDate lastQuarterDate = today.minusMonths(3);
                startDate = lastQuarterDate.with(IsoFields.DAY_OF_QUARTER, 1);
                endDate = startDate.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                groupByDay = false; // Quý thì nên nhóm theo tháng
                break;

            // --- NĂM ---
            case THIS_YEAR:
                startDate = today.with(TemporalAdjusters.firstDayOfYear());
                groupByDay = false; // Năm thì nhóm theo tháng
                break;
            case LAST_YEAR:
            default: // Mặc định là năm trước nếu không khớp
                LocalDate lastYearDate = today.minusYears(1);
                startDate = lastYearDate.with(TemporalAdjusters.firstDayOfYear());
                endDate = lastYearDate.with(TemporalAdjusters.lastDayOfYear());
                groupByDay = false; // Năm thì nhóm theo tháng
                break;
        }

        return getStatsAndFillGaps(startDate, endDate, groupByDay);
    }

    @Override
    public List<StatsDataPoint> getHourlyStatsForDay(LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfNextDay = day.plusDays(1).atStartOfDay();

        List<StatsProjection> dbProjections = orderRepository.findStatsGroupedByHour(
                toDate(startOfDay),
                toDate(endOfNextDay)
        );

        // Chuyển đổi
        List<StatsDataPoint> dbResults = dbProjections.stream()
                .map(p -> new StatsDataPoint(p.getLabel(), p.getRevenue(), p.getOrderCount()))
                .toList();

        // Logic lấp đầy giữ nguyên
        Map<String, StatsDataPoint> resultsMap = dbResults.stream()
                .collect(Collectors.toMap(StatsDataPoint::label, item -> item));

        List<StatsDataPoint> fullDayStats = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            String currentHourLabel = String.format("%02d", hour);
            StatsDataPoint dataPoint = resultsMap.getOrDefault(currentHourLabel,
                    new StatsDataPoint(currentHourLabel, 0L, 0L));
            fullDayStats.add(dataPoint);
        }
        return fullDayStats;
    }

    @Override
    public List<StatsDataPoint> getStatsForCustomRange(LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        final long THRESHOLD_IN_DAYS = 62; // Ngưỡng ~2 tháng

        // Nếu khoảng thời gian nhỏ hơn hoặc bằng ngưỡng, nhóm theo ngày. Ngược lại nhóm theo tháng.
        boolean groupByDay = daysBetween <= THRESHOLD_IN_DAYS;

        return getStatsAndFillGaps(startDate, endDate, groupByDay);
    }

    private List<StatsDataPoint> getStatsAndFillGaps(LocalDate startDate, LocalDate endDate, boolean groupByDay) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        // 1. Lấy kết quả dưới dạng List<StatsProjection>
        List<StatsProjection> dbResults = groupByDay
                ? orderRepository.findStatsGroupedByDay(toDate(startDateTime), toDate(endDateTime))
                : orderRepository.findStatsGroupedByMonth(toDate(startDateTime), toDate(endDateTime));

        // 2. Chuyển đổi từ StatsProjection sang StatsDataPoint
        List<StatsDataPoint> statsDataPoints = dbResults.stream()
                .map(proj -> new StatsDataPoint(proj.getLabel(), proj.getRevenue(), proj.getOrderCount()))
                .collect(Collectors.toList());

        // 3. Lấp đầy dữ liệu (giữ nguyên logic cũ)
        return groupByDay
                ? fillMissingDataByDay(statsDataPoints, startDate, endDate)
                : fillMissingDataByMonth(statsDataPoints, startDate, endDate);
    }
    private List<StatsDataPoint> fillMissingDataByDay(List<StatsDataPoint> dbResults, LocalDate startDate, LocalDate endDate) {
        Map<String, StatsDataPoint> resultsMap = dbResults.stream()
                .collect(Collectors.toMap(StatsDataPoint::label, item -> item));

        List<StatsDataPoint> filledStats = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String currentDayLabel = date.format(DateTimeFormatter.ISO_LOCAL_DATE); // "YYYY-MM-DD"
            StatsDataPoint dataPoint = resultsMap.getOrDefault(currentDayLabel,
                    new StatsDataPoint(currentDayLabel, 0L, 0L));
            filledStats.add(dataPoint);
        }
        return filledStats;
    }
    private List<StatsDataPoint> fillMissingDataByMonth(List<StatsDataPoint> dbResults, LocalDate startDate, LocalDate endDate) {
        Map<String, StatsDataPoint> resultsMap = dbResults.stream()
                .collect(Collectors.toMap(StatsDataPoint::label, item -> item));

        List<StatsDataPoint> filledStats = new ArrayList<>();
        // Chuẩn hóa về ngày đầu tiên của tháng để lặp
        LocalDate startMonth = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        for (LocalDate month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            String currentMonthLabel = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            StatsDataPoint dataPoint = resultsMap.getOrDefault(currentMonthLabel,
                    new StatsDataPoint(currentMonthLabel, 0L, 0L));
            filledStats.add(dataPoint);
        }
        return filledStats;
    }

    /**
     * Chuyển đổi từ LocalDateTime (Java 8+) sang java.util.Date (dùng cho JPA).
     */
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

//    @Override
//    public List<RevenueStatsDTO> getRevenueStats(Date startDate, Date endDate, GroupByPeriod groupBy) {
//        return orderRepository.getRevenueStats(startDate, endDate, groupBy);
//    }
//
//    @Override
//    public List<RevenueOrderStatsDTO> getRevenueAndOrders(String range) {
//        LocalDate startDate = switch (range.toUpperCase()) {
//            case "1M" -> LocalDate.now().minusMonths(1);
//            case "6M" -> LocalDate.now().minusMonths(6);
//            case "1Y" -> LocalDate.now().minusYears(1);
//            default -> LocalDate.of(2000, 1, 1);
//        };
//
//        return orderRepository.getRevenueAndOrdersByMonth(startDate);
//    }
}
