package org.frogcy.furniturecustomer.checkout;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.apache.commons.codec.binary.Hex;
import org.cloudinary.json.JSONObject;
import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.frogcy.furniturecommon.entity.order.PaymentStatus;
import org.frogcy.furniturecustomer.order.OrderNotFoundException;
import org.frogcy.furniturecustomer.order.OrderRepository;
import org.frogcy.furniturecustomer.order.OrderService;
import org.frogcy.furniturecustomer.order.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderServiceImpl orderServiceImpl;

    public CheckoutController(CheckoutService checkoutService, OrderService orderService, OrderRepository orderRepository, OrderServiceImpl orderServiceImpl) {
        this.checkoutService = checkoutService;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderServiceImpl = orderServiceImpl;
    }
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;


    @Value("${zalo.app.id}")
    private String appId;

    @Value("${zalo.app.key.1}")
    private String key1;

    @Value("${zalo.app.key.2}")
    private String key2;

    private final String createOrderUrl = "https://sb-openapi.zalopay.vn/v2/create" ;

    @Value("${ngrok.domain}")
    private String ngrokDomain;

    private final String callbackUrl =  ngrokDomain + "/api/checkout/zalopay/callback";
    private final String redirectUrl = "http://localhost:3000/checkout/success";



    @PostMapping("/stripe/create")
    public ResponseEntity<?> createCheckoutWithStripe(@RequestBody Map<String, Object> data) throws Exception{
        Map<String, Object> response = checkoutService.createStripeCheckoutSession(data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody String payload,
                                           @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try{
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
        // Kiểm tra loại event
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject().orElse(null);

            if (session != null && "paid".equals(session.getPaymentStatus())) {
                String orderId = session.getMetadata().get("orderId");
                String paymentIntentId = session.getPaymentIntent();
                System.out.println("✅ Thanh toán thành công cho orderId=" + orderId);

                // TODO: cập nhật trạng thái đơn hàng trong DB:
                // orderService.updatePaymentStatus(orderId, PaymentStatus.PAID);
                orderService.updatePaymentStatus(orderId,paymentIntentId, PaymentStatus.PAID);
            }
        } else if("charge.refunded".equals(event.getType())){
            Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
            if (charge != null) {
                String paymentIntentId = charge.getPaymentIntent();
                Optional<Order> orderOpt = orderRepository.findByPaymentIntentId(paymentIntentId);
                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();
                    order.setPaymentStatus(PaymentStatus.REFUNDED);
                    order.setStatus(OrderStatus.RETURNED);
                    orderRepository.save(order);

                    System.out.println("✅ Hoàn tiền thành công cho orderId=" + order.getId());
                }
                orderService.updateRefundSuccess(paymentIntentId, PaymentStatus.REFUNDED);
            }
        }

        return ResponseEntity.ok("Webhook received");
    }

    @PostMapping("/zalopay/create")
    public Map<String, Object> createCheckoutWithZaloPay(@RequestBody Map<String, Object> request) throws Exception{

        System.out.println("Request body: " + request);
        Integer orderId = ((Number) request.get("orderId")).intValue();
        Order orderDB = orderRepository.findById(orderId).orElseThrow(
                ()-> new OrderNotFoundException("Order not found with orderId=" + orderId)
        );

        String appUser = "Hoang Ha company";
        long amount = orderDB.getTotal();

        // --- 1. Tạo app_trans_id theo giờ VN ---
        TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance(tz);
        String yyMMdd = new SimpleDateFormat("yyMMdd").format(calendar.getTime());
        String appTransId = yyMMdd + "_" + System.currentTimeMillis();

        // --- 2. app_time ---
        long appTime = System.currentTimeMillis();

        // --- 3. embed_data & item (chuỗi JSON) ---
        String embedData = "{\"redirecturl\": \"" + redirectUrl + "\", \"orderId\": \"" + orderId + "\"}";
               String item = "[]";

        // --- 4. Tạo description ---
        String description = " - Thanh toán cho đơn hàng #" + appTransId;

        // --- 5. Tạo hmac_input và mac ---
        String hmacInput = appId + "|" + appTransId + "|" + appUser + "|" + amount + "|" + appTime + "|" + embedData + "|" + item;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key1.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String macValue = Hex.encodeHexString(mac.doFinal(hmacInput.getBytes(StandardCharsets.UTF_8)));

        // --- 6. Chuẩn bị body gửi ZaloPay ---
        Map<String, Object> order = new HashMap<>();
        order.put("app_id", Integer.parseInt(appId));
        order.put("app_user", appUser);
        order.put("app_time", appTime);
        order.put("amount", amount);
        order.put("app_trans_id", appTransId);
        order.put("bank_code", "zalopayapp");
        order.put("embed_data", embedData);
        order.put("item", item);
        order.put("callback_url", callbackUrl);
        order.put("redirect_url", redirectUrl);
        order.put("description", description);
        order.put("mac", macValue);

        System.out.println("appUser=" + appUser);
        System.out.println("amount=" + amount);
        System.out.println("app_trans_id=" + appTransId);
        System.out.println("app_time=" + appTime);
        System.out.println("hmacInput=" + hmacInput);
        System.out.println("mac=" + macValue);

        // --- 7. Gửi request POST sang ZaloPay sandbox ---
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(createOrderUrl, order, Map.class);

        System.out.println("ZaloPay response: " + response);
        return response;
    }

    @PostMapping(value = "/zalopay/callback", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> zaloPayCallback(@RequestBody Map<String, Object> callbackData) throws Exception {
        System.out.println("ZaloPay callback raw body: " + callbackData);

        String data = (String) callbackData.get("data");
        String mac = (String) callbackData.get("mac");
        Integer type = callbackData.get("type") != null ? ((Number) callbackData.get("type")).intValue() : null;

        if (data == null || mac == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "return_code", -1,
                    "return_message", "Missing data or mac"
            ));
        }

        // --- 1. Tính lại MAC bằng key2 ---
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(key2.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String computedMac = Hex.encodeHexString(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));

        if (!computedMac.equals(mac)) {
            System.out.println("⚠️ MAC không khớp — Callback không hợp lệ!");
            return ResponseEntity.ok(Map.of(
                    "return_code", -1,
                    "return_message", "mac not match"
            ));
        }

        // --- 2. Parse JSON data ---
        JSONObject jsonData = new JSONObject(data);
        String appTransId = jsonData.optString("app_trans_id");
        long amount = jsonData.optLong("amount");
        int status = jsonData.optInt("status", 1); // 1 = success theo docs
        String zpTransId = String.valueOf(jsonData.optLong("zp_trans_id"));
        String embedData = jsonData.optString("embed_data", "{}");

        System.out.println("✅ Callback hợp lệ cho app_trans_id=" + appTransId + ", type=" + type);

        // --- 3. Nếu thanh toán thành công ---
        if (status == 1) {
            JSONObject embedJson = new JSONObject(embedData);
            String orderId = embedJson.optString("orderId", "UNKNOWN");

            System.out.println("💰 Thanh toán thành công cho orderId=" + orderId + ", amount=" + amount);

            orderService.updatePaymentStatus(orderId, zpTransId, PaymentStatus.PAID);
        } else {
            System.out.println("❌ Thanh toán thất bại hoặc bị hủy. status=" + status);
        }

        // --- 4. Trả phản hồi cho ZaloPay ---
        Map<String, Object> response = new HashMap<>();
        response.put("return_code", 1);
        response.put("return_message", "success");

        return ResponseEntity.ok(response);
    }


    @PostMapping("/zalopay/refund")
    public Map<String, Object> refundZaloPay(@RequestBody Map<String, Object> request) throws Exception {
        System.out.println("Refund request body: " + request);
        String orderId = (String) request.get("orderId");



        // --- 1. Lấy thông tin từ request ---
        String zpTransId = (String) request.get("zp_trans_id");  // Mã giao dịch ZaloPay
        Long amount = ((Number) request.get("amount")).longValue(); // Số tiền muốn hoàn
        String description = (String) request.get("description"); // Lý do hoàn tiền

        if (zpTransId == null || amount == null || description == null) {
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

        System.out.println("ZaloPay refund response: " + response);
        return response;
    }


}
