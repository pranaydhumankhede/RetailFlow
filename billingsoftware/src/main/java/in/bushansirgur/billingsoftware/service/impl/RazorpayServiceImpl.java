package in.pranay.billingsoftware.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import in.pranay.billingsoftware.io.OrderResponse;
import in.pranay.billingsoftware.io.RazorpayOrderResponse;
import in.pranay.billingsoftware.service.RazorpayService;
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
        // Trim whitespace from credentials loaded from environment
        if (razorpayKeyId != null) {
            razorpayKeyId = razorpayKeyId.trim();
        }
        if (razorpayKeySecret != null) {
            razorpayKeySecret = razorpayKeySecret.trim();
        }

        isRazorpayConfigured = razorpayKeyId != null && !razorpayKeyId.isBlank() &&
                razorpayKeySecret != null && !razorpayKeySecret.isBlank();

        if (!isRazorpayConfigured) {
            log.warn("⚠️  RAZORPAY NOT CONFIGURED");
            log.warn("   RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables not set");
            log.warn("   Get them from: https://dashboard.razorpay.com/app/keys");
            log.warn("   Payment endpoints will return errors until configured");
        } else {
            log.info("✓ Razorpay integration configured successfully");
            log.debug("Razorpay Key ID starts with: {}", razorpayKeyId.substring(0, Math.min(8, razorpayKeyId.length())));
        }
    }

    @Override
    public RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException {
        if (!isRazorpayConfigured) {
            throw new RazorpayException(
                    "Razorpay is not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET environment variables.");
        }

        try {
            // Debug: Print exact credentials being used
            String keyId = razorpayKeyId != null ? razorpayKeyId : "NULL";
            String keySecret = razorpayKeySecret != null ? razorpayKeySecret : "NULL";
            
            System.out.println("\n=== RAZORPAY DEBUG ===");
            System.out.println("Key ID: [" + keyId + "]");
            System.out.println("Key ID length: " + keyId.length());
            System.out.println("Key ID bytes: " + java.util.Arrays.toString(keyId.getBytes()));
            System.out.println("Key Secret: [" + keySecret + "]");
            System.out.println("Key Secret length: " + keySecret.length());
            System.out.println("Key Secret bytes: " + java.util.Arrays.toString(keySecret.getBytes()));
            System.out.println("Amount: " + amount);
            System.out.println("Currency: " + currency);
            System.out.println("======================\n");
            
            log.debug("Creating Razorpay order - Amount: " + amount + ", Currency: " + currency);
            
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);

            log.debug("Order request payload: " + orderRequest.toString());
            Order order = razorpayClient.orders.create(orderRequest);
            log.debug("Order created successfully with ID: " + order.get("id"));
            return convertToResponse(order);
        } catch (RazorpayException e) {
            System.out.println("\n!!! RAZORPAY ERROR !!!");
            System.out.println("Message: " + e.getMessage());
            System.out.println("Exception: " + e);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!\n");
            log.error("Razorpay API error: " + e.getMessage());
            throw e;
        }
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
