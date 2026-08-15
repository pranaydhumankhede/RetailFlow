package in.bushansirgur.billingsoftware.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import in.bushansirgur.billingsoftware.io.OrderResponse;
import in.bushansirgur.billingsoftware.io.RazorpayOrderResponse;
import in.bushansirgur.billingsoftware.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/**
 * Razorpay Payment Service
 * For development without Razorpay, leave RAZORPAY_KEY_ID and
 * RAZORPAY_KEY_SECRET empty.
 * Payment endpoints will return an error message for testing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayServiceImpl implements RazorpayService {

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    private boolean isRazorpayConfigured;

    /**
     * Check if Razorpay credentials are available on startup.
     * Does NOT throw exception - allows app to start without Razorpay for
     * development.
     */
    @PostConstruct
    public void validateRazorpayCredentials() {
        isRazorpayConfigured = razorpayKeyId != null && !razorpayKeyId.isBlank() &&
                razorpayKeySecret != null && !razorpayKeySecret.isBlank();

        if (!isRazorpayConfigured) {
            log.warn("⚠️  RAZORPAY NOT CONFIGURED");
            log.warn("   RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables not set");
            log.warn("   Get them from: https://dashboard.razorpay.com/app/keys");
            log.warn("   Payment endpoints will return errors until configured");
        } else {
            log.info("✓ Razorpay integration configured successfully");
        }
    }

    @Override
    public RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException {
        if (!isRazorpayConfigured) {
            throw new RazorpayException(
                    "Razorpay is not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
        }

        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100);
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(orderRequest);
        return convertToResponse(order);
    }

    private RazorpayOrderResponse convertToResponse(Order order) {
        return RazorpayOrderResponse.builder()
                .id(order.get("id"))
                .entity(order.get("entity"))
                .amount(order.get("amount"))
                .currency(order.get("currency"))
                .status(order.get("status"))
                .created_at(order.get("created_at"))
                .receipt(order.get("receipt"))
                .build();
    }
}
