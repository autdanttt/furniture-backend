package org.frogcy.furnitureadmin.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.frogcy.furnitureadmin.dashboard.dto.GroupByPeriod;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueOrderStatsDTO;
import org.frogcy.furnitureadmin.dashboard.dto.RevenueStatsDTO;
import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.frogcy.furniturecommon.entity.order.PaymentStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<RevenueStatsDTO> getRevenueStats(Date startDate, Date endDate, GroupByPeriod groupBy) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<Order> orderRoot = cq.from(Order.class);

        // --- Filter điều kiện cơ bản ---
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderRoot.get("status"), OrderStatus.DELIVERED)); // chỉ tính doanh thu đã giao
        predicates.add(cb.equal(orderRoot.get("paymentStatus"), PaymentStatus.PAID));
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(orderRoot.get("orderTime"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(orderRoot.get("orderTime"), endDate));
        }

        // --- Group by thời gian ---
        Expression<?> groupExpression;
        Expression<String> formattedDate;

        switch (groupBy) {
            case MONTH:
                groupExpression = cb.function("DATE_FORMAT", String.class, orderRoot.get("orderTime"), cb.literal("%Y-%m"));
                formattedDate = cb.function("DATE_FORMAT", String.class, orderRoot.get("orderTime"), cb.literal("%Y-%m"));
                break;
            case YEAR:
                groupExpression = cb.function("YEAR", Integer.class, orderRoot.get("orderTime"));
                formattedDate = cb.function("YEAR", String.class, orderRoot.get("orderTime"));
                break;
            default: // DAY
                groupExpression = cb.function("DATE", Date.class, orderRoot.get("orderTime"));
                formattedDate = cb.function("DATE_FORMAT", String.class, orderRoot.get("orderTime"), cb.literal("%Y-%m-%d"));
                break;
        }

        // --- Select ---
        cq.multiselect(
                        formattedDate.alias("period"),
                        cb.sum(orderRoot.get("total")).alias("totalRevenue")
                )
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(groupExpression)
                .orderBy(cb.asc(groupExpression));

        List<Tuple> results = em.createQuery(cq).getResultList();

        // --- Map kết quả về DTO ---
        return results.stream()
                .map(t -> new RevenueStatsDTO(
                        t.get("period", String.class),
                        t.get("totalRevenue", Long.class)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RevenueOrderStatsDTO> getRevenueAndOrdersByMonth(LocalDate startDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<Order> order = cq.from(Order.class);

        // --- Lọc trạng thái ---
        Predicate statusPredicate = order.get("status").in(
//                OrderStatus.NEW,
//                OrderStatus.PROCESSING,
//                OrderStatus.PACKAGED,
//                OrderStatus.PICKED,
//                OrderStatus.SHIPPING,
                OrderStatus.DELIVERED
        );

        // --- Lọc theo ngày bắt đầu ---
        Predicate datePredicate = cb.greaterThanOrEqualTo(order.get("orderTime"), startDate.atStartOfDay());

        // --- Tạo biểu thức tháng ---
        Expression<Integer> monthExp = cb.function("month", Integer.class, order.get("orderTime"));

        // --- Gom nhóm theo tháng ---
        cq.multiselect(
                        monthExp.alias("month"),
                        cb.coalesce(cb.sum(order.get("total")), 0L).alias("revenue"),
                        cb.count(order.get("id")).alias("orders")
                )
                .where(cb.and(statusPredicate, datePredicate))
                .groupBy(monthExp)
                .orderBy(cb.asc(monthExp));

        List<Tuple> tuples = em.createQuery(cq).getResultList();

        // --- Map về DTO ---
        return tuples.stream()
                .map(t -> new RevenueOrderStatsDTO(
                        t.get("month", Integer.class),
                        t.get("revenue", Long.class),
                        t.get("orders", Long.class)
                ))
                .toList();
    }

}
