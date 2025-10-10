package org.frogcy.furniturecustomer.checkout;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.frogcy.furniturecommon.entity.order.PaymentStatus;
import org.frogcy.furniturecustomer.order.OrderRepository;
import org.frogcy.furniturecustomer.order.OrderService;
import org.frogcy.furniturecustomer.order.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

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

}
