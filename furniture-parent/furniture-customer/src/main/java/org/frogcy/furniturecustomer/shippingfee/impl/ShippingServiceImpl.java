package org.frogcy.furniturecustomer.shippingfee.impl;

import org.frogcy.furniturecommon.entity.CartItem;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.ShippingFee;
import org.frogcy.furniturecommon.entity.address.Province;
import org.frogcy.furniturecustomer.cart.CartItemRepository;
import org.frogcy.furniturecustomer.shippingfee.CartEmptyException;
import org.frogcy.furniturecustomer.shippingfee.ShippingFeeRepository;
import org.frogcy.furniturecustomer.shippingfee.ShippingNotFoundException;
import org.frogcy.furniturecustomer.shippingfee.ShippingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingServiceImpl implements ShippingService {
    private static final int SHIPPING_FACTOR = 5000; // dùng theo cm³/kg

    private static final Long FREE_SHIPPING_THRESHOLD = 500000L;
    private final CartItemRepository cartItemRepository;
    private final ShippingFeeRepository shippingFeeRepository;

    public ShippingServiceImpl(ShippingFeeRepository shippingFeeRepository, CartItemRepository cartItemRepository) {
        this.shippingFeeRepository = shippingFeeRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public long calculate(Integer provinceCode, Customer customer) {
        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        if (cartItems.isEmpty()) {
            throw new CartEmptyException("No cart items found for customer " + customer.getId());
        }

        // 1️⃣ Tính trọng lượng quy đổi (dim weight)
        double chargeableWeight = getChargeableWeight(cartItems);

        // 2️⃣ Lấy phí cơ bản theo tỉnh
        ShippingFee feeConfig = shippingFeeRepository.findByProvinceCode(provinceCode)
                .orElseThrow(() -> new ShippingNotFoundException("Không tìm thấy phí ship cho tỉnh " + provinceCode));

        long baseFee = feeConfig.getFee(); // nếu fee là BigDecimal

        // 3️⃣ Tính phụ phí theo trọng lượng
        double extraFee = 0;
        if (chargeableWeight > 1.0) { // 1kg đầu tiên tính baseFee
            double extraWeight = chargeableWeight - 1.0;
            extraFee = Math.ceil(extraWeight / 0.5) * 1000; // mỗi 0.5kg thêm 1k
        }

        // 4️⃣ Tổng phí ship
        return baseFee + (long) extraFee;
    }

    private static double getChargeableWeight(List<CartItem> cartItems) {
        double totalWeight = 0;
        double totalVolume = 0;

        for (CartItem item : cartItems) {
            var product = item.getProduct();
            int qty = item.getQuantity();

            totalWeight += product.getWeight() * qty;
            totalVolume += (product.getLength() * product.getWidth() * product.getHeight()) * qty;
        }

        double volumetricWeight = totalVolume / 9000.0;
        return Math.max(totalWeight, volumetricWeight);
    }

}
