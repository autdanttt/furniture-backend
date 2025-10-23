package org.frogcy.furniturecustomer.checkout.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderDetail;
import org.frogcy.furniturecustomer.checkout.CheckoutService;
import org.frogcy.furniturecustomer.order.OrderNotFoundException;
import org.frogcy.furniturecustomer.order.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final OrderRepository orderRepository;
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public CheckoutServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Map<String, Object> createStripeCheckoutSession(Map<String, Object> data) throws Exception {
        Stripe.apiKey = stripeApiKey;

        Integer orderId = ((Number) data.get("orderId")).intValue();
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id: " + orderId)
        );

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        lineItems.add(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("vnd")
                                        .setUnitAmount(order.getTotal())
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Thanh toan order #" + order.getId())
                                                        .addImage("https://res.cloudinary.com/dm8tfyppk/image/upload/v1759893179/product/a4600b7d-9a3f-4927-ac20-2569fa972037.jpg")
                                                        .build()
                                        ).build()
                        ).build()
        );


        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setSuccessUrl("http://localhost:3000/order")
                .setCancelUrl("http://localhost:3000/checkout/cancel")
                .addAllLineItem(lineItems)
                .putMetadata("orderId", order.getId().toString())
                .build();

        Session session = Session.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("url", session.getUrl());
        response.put("id", session.getId());

        return response;
    }
}
