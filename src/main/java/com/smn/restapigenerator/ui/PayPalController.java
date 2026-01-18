package com.smn.restapigenerator.ui;

import com.smn.restapigenerator.service.PayPalService;
import com.smn.restapigenerator.util.URLUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
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

  private final PayPalService payPalService;

  public PayPalController(PayPalService payPalService) {
  this.payPalService = payPalService;
  }

  public record CreateOrderRequest(String itemName, String itemId) {}

  @PostMapping("/orders")
  public Map<String, Object> createOrder(@RequestBody CreateOrderRequest reqBody, HttpServletRequest request) {
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
    return Map.of("id", orderId);
  }

  @PostMapping("/orders/{orderId}/capture")
  public Map<String, Object> capture(@PathVariable String orderId, HttpServletRequest request) {
    boolean useSandbox = URLUtil.isFromLocalhost(request);
    return payPalService.captureOrder(orderId, useSandbox);
  }

}
