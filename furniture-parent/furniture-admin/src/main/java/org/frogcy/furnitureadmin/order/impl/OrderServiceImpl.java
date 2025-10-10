package org.frogcy.furnitureadmin.order.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import org.apache.commons.codec.binary.Hex;
import org.frogcy.furnitureadmin.inventory.InventoryRepository;
import org.frogcy.furnitureadmin.order.OrderRepository;
import org.frogcy.furnitureadmin.order.OrderService;
import org.frogcy.furnitureadmin.order.OrderTrackRepository;
import org.frogcy.furnitureadmin.order.dto.*;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.order.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderTrackRepository orderTrackRepository;
    private final OrderTrackMapper orderTrackMapper;
    private final InventoryRepository inventoryRepository;


    @Value("${zalo.app.id}")
    private String appId;

    @Value("${zalo.app.key.1}")
    private String key1;

    @Value("${zalo.app.key.2}")
    private String key2;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, OrderTrackRepository orderTrackRepository, OrderTrackMapper orderTrackMapper, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderTrackRepository = orderTrackRepository;
        this.orderTrackMapper = orderTrackMapper;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public PageResponseDTO<OrderSummaryDTO> getOrders(int page, int size, String sortField, String sortDir, String keyword) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();


        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.search(keyword, pageable);
        List<OrderSummaryDTO> orders = orderPage.getContent().stream()
                .map(order -> {
                    OrderSummaryDTO orderSummaryDTO = orderMapper.toSummaryDTO(order);
                    orderSummaryDTO.setStatus(order.getStatus());
                    return orderSummaryDTO;
                }).toList();

        return new PageResponseDTO<>(
                orders,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }

    @Transactional
    @Override
    public void updateStatus(Integer orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + orderId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isShipper = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SHIPPER"));

        OrderStatus newStatus = request.getStatus();

        // ✅ Nếu là shipper → chỉ cho phép cập nhật PICKED, SHIPPING, DELIVERED
        if (isShipper) {
            if (!(newStatus == OrderStatus.PICKED ||
                    newStatus == OrderStatus.SHIPPING ||
                    newStatus == OrderStatus.DELIVERED)) {
                throw new AccessDeniedException("Shipper cannot update to this status");
            }
        }
        // ✅ Nếu là Admin → có thể cập nhật bất kỳ trạng thái nào
        order.setStatus(newStatus);

        // ✅ Nếu giao thành công và là COD → cập nhật trạng thái thanh toán
        if (newStatus == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }


        // Tạo lịch sử trạng thái (OrderTrack)
        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus(request.getStatus());
        track.setNotes(
                request.getNotes() != null ? request.getNotes() : newStatus.defaultDescription()
        );
        track.setUpdatedTime(new Date());
        orderTrackRepository.save(track);

        orderRepository.save(order);
    }

    @Override
    public OrderResponseDTO get(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));

        return getOrderResponseDTO(order);
    }

    @Transactional
    @Override
    public void approveReturn(Integer orderId, String apiKey) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("No order found for id " + orderId)
        );

        Date now = new Date();

        try {
            // Xử lý hoàn tiền tùy theo phương thức thanh toán
            if (order.getPaymentMethod() == PaymentMethod.STRIPE) {
                Stripe.apiKey = apiKey;
                RefundCreateParams params = RefundCreateParams.builder()
                        .setPaymentIntent(order.getPaymentIntentId()) // ✅ đúng tên
                        .build();

                Refund refund = Refund.create(params);

                order.setPaymentStatus(PaymentStatus.REFUNDED); // ✅ Stripe trả kết quả ngay
                order.setStatus(OrderStatus.RETURNED);

            } else if (order.getPaymentMethod() == PaymentMethod.COD) {
                // COD không gọi Stripe, hoàn tiền thủ công
                order.setPaymentStatus(PaymentStatus.REFUNDED);
                order.setStatus(OrderStatus.RETURNED);
            }else if(order.getPaymentMethod() == PaymentMethod.ZALOPAY){
//                System.out.println("Refund request body: " + request);


                // --- 1. Lấy thông tin từ request ---
                String zpTransId = order.getPaymentIntentId(); // Mã giao dịch ZaloPay
                Long amount = order.getTotal(); // Số tiền muốn hoàn
                String description = "Chap nhan hoan tien"; // Lý do hoàn tiền

                if (zpTransId == null || amount == null) {
                    throw new IllegalArgumentException("zp_trans_id, amount, description bắt buộc phải có");
                }

                // --- 2. Tạo m_refund_id ---
                TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
                Calendar calendar = Calendar.getInstance(tz);
                String yyMMdd = new SimpleDateFormat("yyMMdd").format(calendar.getTime());
                String mRefundId = yyMMdd + "_" + appId + "_" + System.currentTimeMillis();

                // --- 3. Timestamp hiện tại ---
                long timestamp = System.currentTimeMillis();

                // --- 4. Tạo hmac_input và mac ---
                String hmacInput = appId + "|" + zpTransId + "|" + amount + "|" + description + "|" + timestamp;
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(key1.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                String macValue = Hex.encodeHexString(mac.doFinal(hmacInput.getBytes(StandardCharsets.UTF_8)));

                // --- 5. Chuẩn bị body gửi ZaloPay ---
                Map<String, Object> body = new HashMap<>();
                body.put("app_id", Integer.parseInt(appId));
                body.put("m_refund_id", mRefundId);
                body.put("zp_trans_id", zpTransId);
                body.put("amount", amount);
                body.put("description", description);
                body.put("timestamp", timestamp);
                body.put("mac", macValue);

                // --- 6. Gửi request POST sang sandbox ---
                RestTemplate restTemplate = new RestTemplate();
                String refundUrl = "https://sb-openapi.zalopay.vn/v2/refund"; // sandbox
                Map<String, Object> response = restTemplate.postForObject(refundUrl, body, Map.class);
                Integer responseCode = (Integer) response.get("return_code");
//                String refundId = (String) response.get("refund_id");
                if(responseCode == 1){
                    order.setStatus(OrderStatus.RETURNED);
                    order.setPaymentStatus(PaymentStatus.REFUNDED);
                } else if (responseCode == 3) {
                    order.setStatus(OrderStatus.RETURNED);
                    order.setPaymentStatus(PaymentStatus.REFUNDING);
                }else if (responseCode == 2) {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                }

                System.out.println("ZaloPay refund response: " + response);
            }

            // ✅ Cập nhật lại số lượng hàng trong kho
            for (OrderDetail detail : order.getOrderDetails()) {
                Inventory inv = inventoryRepository.findByProduct(detail.getProduct())
                        .orElseThrow(() -> new IllegalStateException("Inventory not found for product"));
                inv.setQuantity(inv.getQuantity() + detail.getQuantity());
            }

            // ✅ Lưu track log
            OrderTrack track = new OrderTrack();
            track.setOrder(order);
            track.setStatus(order.getStatus());
            track.setNotes("Return approved and refunded successfully");
            track.setUpdatedTime(now);
            orderTrackRepository.save(track);

            orderRepository.save(order);

        } catch (StripeException | NoSuchAlgorithmException e) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);

            throw new IllegalStateException("Stripe refund failed: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    @Override
    public void rejectReturn(Integer orderId, String reason) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("No order found for id " + orderId)
        );

        if (order.getStatus() != OrderStatus.RETURN_REQUESTED) {
            throw new IllegalStateException("Order status must be RETURN_REQUESTED to reject");
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus(OrderStatus.RETURN_REJECTED);
        order.setPaymentStatus(PaymentStatus.COMPLETED); // Không hoàn tiền
        orderRepository.save(order);

        // Ghi lại lịch sử đơn hàng
        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus(OrderStatus.RETURN_REJECTED);
        track.setNotes("Return request rejected: " + reason);
        track.setUpdatedTime(new Date());
        orderTrackRepository.save(track);
    }


    private static OrderResponseDTO getOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setFirstName(order.getFirstName());
        dto.setLastName(order.getLastName());
        dto.setEmail(order.getEmail());
        dto.setProvinceName(order.getProvinceName());
        dto.setWardName(order.getWardName());
        dto.setAddressLine(order.getAddressLine());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setShippingCost(order.getShippingCost());
        dto.setProductCost(order.getProductCost());
        dto.setSubtotal(order.getSubtotal());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setTotal(order.getTotal());
        dto.setOrderTime(order.getOrderTime());
        dto.setDeliverDays(order.getDeliverDays());
        dto.setDeliverDate(order.getDeliverDate());

        Set<OrderDetailDTO> details = getOrderDetailDTOS(order);
        List<OrderTrackDTO> orderTracks = getOrderTrackDTOS(order);

        dto.setDetails(details);
        dto.setOrderTracks(orderTracks);
        return dto;
    }

    private static List<OrderTrackDTO> getOrderTrackDTOS(Order order) {
        return order.getOrderTracks().stream()
                .map(orderTrack ->{
                        OrderTrackDTO dto = new OrderTrackDTO();
                        dto.setId(orderTrack.getId());
                        dto.setStatus(orderTrack.getStatus());
                        dto.setNotes(orderTrack.getNotes());
                        dto.setUpdatedTime(orderTrack.getUpdatedTime());
                        return dto;
                }).toList();
    }

    private static Set<OrderDetailDTO> getOrderDetailDTOS(Order order) {
        Set<OrderDetailDTO> details = new HashSet<>();

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            OrderDetailDTO detailDTO = new OrderDetailDTO();
            detailDTO.setProductId(orderDetail.getProduct().getId());
            detailDTO.setProductName(orderDetail.getProduct().getName());
            detailDTO.setQuantity(orderDetail.getQuantity());
            detailDTO.setImageUrl(orderDetail.getProduct().getMainImage().getImageUrl());
            detailDTO.setPrice(orderDetail.getUnitPrice());

            details.add(detailDTO);
        }
        return details;
    }
}
