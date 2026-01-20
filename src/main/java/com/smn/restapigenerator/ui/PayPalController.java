package com.smn.restapigenerator.ui;

import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.service.PayPalService;
import com.smn.restapigenerator.service.UserService;
import com.smn.restapigenerator.util.StringUtil;
import com.smn.restapigenerator.util.URLUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {

	private static final Logger logger = LoggerFactory.getLogger(PayPalController.class);

  public record CreateOrderRequest(String itemName, String itemId) {}

  public record PurchaseDetails(
    String orderId,
    String itemId,
    String buyerFullName,
    String paymentStatus,
    String paymentId,
    String paymentAmount,
    String payPalFee) {}

  private final PayPalService payPalService;

  @Autowired
  private UserService service;

  public PayPalController(PayPalService payPalService) {
    this.payPalService = payPalService;
  }

  @PostMapping("/orders")
  public Map<String, Object> createOrder(@RequestBody CreateOrderRequest reqBody, HttpServletRequest request, HttpSession session) {
    if (reqBody.itemId() == null || reqBody.itemId().isBlank() || reqBody.itemName() == null || reqBody.itemName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemName and itemId are required");
    }

    String itemName = reqBody.itemName();
    String itemId = reqBody.itemId();
    String price;
    switch (itemId) {
      case "RAG-LIC-1D":
        price = String.valueOf(UIController.LICENSE_COST_ONE_DAY);
        break;
      case "RAG-LIC-1W":
        price = String.valueOf(UIController.LICENSE_COST_ONE_WEEK);
        break;
      case "RAG-LIC-1M":
        price = String.valueOf(UIController.LICENSE_COST_ONE_MONTH);
        break;
      default:
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown itemId: " + itemId);
    }

    boolean useSandbox = URLUtil.isFromLocalhost(request);
    String orderId = payPalService.createOrder(itemName, itemId, price, "USD", useSandbox);

    User user = (User) session.getAttribute("user");
    if (user != null) {
        logger.info("User {} created renewal order {} for item {}", user.getEmail(), orderId, itemId);
    }

    return Map.of("id", orderId);
  }

  @PostMapping("/orders/{orderId}/capture")
  public Map<String, Object> capture(
    @PathVariable String orderId,
    HttpServletRequest request,
    HttpSession session) {

    boolean useSandbox = URLUtil.isFromLocalhost(request);
    Map<String, Object> response = payPalService.captureOrder(orderId, useSandbox);

		User user = (User) session.getAttribute("user");
    if (user == null) {
      response.put("newExpirationDate", "INVALID");

    } else {
      String email = user.getEmail();

      PurchaseDetails purchaseDetails = PayPalController.extractPurchaseDetails(response);
      if ("COMPLETED".equalsIgnoreCase(purchaseDetails.paymentStatus())) {
        logger.info("User {} renewed license: {}", email, purchaseDetails);

        switch (purchaseDetails.itemId()) {
          case "RAG-LIC-1D": {
            Date newExpirationDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
            service.updateUser(user, newExpirationDate);
            response.put("newExpirationDate", StringUtil.formatDate(newExpirationDate));
          } break;
          case "RAG-LIC-1W": {
            Date newExpirationDate = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));
            service.updateUser(user, newExpirationDate);
            response.put("newExpirationDate", StringUtil.formatDate(newExpirationDate));
          } break;
          case "RAG-LIC-1M": {
            Date newExpirationDate = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));
            service.updateUser(user, newExpirationDate);
            response.put("newExpirationDate", StringUtil.formatDate(newExpirationDate));
          } break;
          default: {
            logger.error("Unknown itemId for license renewal: {}", purchaseDetails.itemId());
          } break;
        }

      } else {
        logger.info("User {} failed to renew license.  Order status is {}", email, purchaseDetails.paymentStatus());
      }
    }

    return response;
  }

  /**
   * Converts a PayPal capture response into a PurchaseDetails DTO.
   * 
   * @param paypalResponse The PayPal capture response as returned by the PayPal API
   * @return PurchaseDetails DTO with extracted payment information
   */
  @SuppressWarnings("unchecked")
  private static PurchaseDetails extractPurchaseDetails(Map<String, Object> paypalResponse) {
    String orderId = null;
    String itemId = null;
    String buyerFullName = null;
    String paymentStatus = null;
    String paymentId = null;
    String paymentAmount = null;
    String payPalFee = null;

    try {
      // Extract order ID
      orderId = (String) paypalResponse.get("id");

      // Extract purchase units
      List<Map<String, Object>> purchaseUnits = (List<Map<String, Object>>) paypalResponse.get("purchase_units");
      if (purchaseUnits != null && !purchaseUnits.isEmpty()) {
        Map<String, Object> purchaseUnit = purchaseUnits.get(0);
        
        // Extract capture details
        Map<String, Object> payments = (Map<String, Object>) purchaseUnit.get("payments");
        if (payments != null) {
          List<Map<String, Object>> captures = (List<Map<String, Object>>) payments.get("captures");
          if (captures != null && !captures.isEmpty()) {
            Map<String, Object> capture = captures.get(0);
            
            // Extract item ID from custom_id
            itemId = (String) capture.get("custom_id");

            // Extract payment ID
            paymentId = (String) capture.get("id");
            
             // Extract payment status
            paymentStatus = (String) capture.get("status");
            
            // Extract payment amount
            Map<String, Object> amount = (Map<String, Object>) capture.get("amount");
            if (amount != null) {
              paymentAmount = (String) amount.get("value");
            }

            // Extract seller receivable breakdown for PayPal fee
            Map<String, Object> sellerReceivableBreakdown = (Map<String, Object>) capture.get("seller_receivable_breakdown");
            if (sellerReceivableBreakdown != null) {
              Map<String, Object> paypalFeeInfo = (Map<String, Object>) sellerReceivableBreakdown.get("paypal_fee");
              if (paypalFeeInfo != null) {
                payPalFee = (String) paypalFeeInfo.get("value");
              }
            }
          }
        }
      }

      // Extract payer information
      Map<String, Object> payer = (Map<String, Object>) paypalResponse.get("payer");
      if (payer != null) {
        Map<String, Object> name = (Map<String, Object>) payer.get("name");
        if (name != null) {
          String givenName = (String) name.get("given_name");
          String surname = (String) name.get("surname");
          if (givenName != null && surname != null) {
            buyerFullName = givenName + " " + surname;
          } else if (givenName != null) {
            buyerFullName = givenName;
          } else if (surname != null) {
            buyerFullName = surname;
          }
        }
      }

    } catch (Exception e) {
      // Log the exception or handle it as needed
      System.err.println("Error converting PayPal response to CaptureResponse: " + e.getMessage());
    }

    return new PurchaseDetails(
      orderId,
      itemId,
      buyerFullName,
      paymentStatus,
      paymentId,
      paymentAmount,
      payPalFee
    );
  }

}
