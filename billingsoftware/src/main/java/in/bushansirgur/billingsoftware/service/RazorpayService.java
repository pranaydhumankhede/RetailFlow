package in.pranay.billingsoftware.service;

import com.razorpay.RazorpayException;
import in.pranay.billingsoftware.io.RazorpayOrderResponse;

public interface RazorpayService {

    RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException;
}
