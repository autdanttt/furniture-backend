package org.frogcy.furniturecustomer.checkout;

import com.stripe.exception.StripeException;

import java.util.Map;

public interface CheckoutService {
    Map<String, Object> createStripeCheckoutSession(Map<String, Object> data) throws Exception;
}
