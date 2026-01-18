package com.smn.restapigenerator.ui;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.smn.restapigenerator.service.PayPalService;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {

  private final PayPalService payPalService;

  public PayPalController(PayPalService payPalService) {
    this.payPalService = payPalService;
  }

  public record CreateOrderRequest(String itemName, String itemId) {}

  @PostMapping("/orders")
  public Map<String, Object> createOrder(@RequestBody CreateOrderRequest req) {
    if (req.itemId() == null || req.itemId().isBlank() || req.itemName() == null || req.itemName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemName and itemId are required");
    }

    String itemName = req.itemName();
    String itemId = req.itemId();
    String price;
    switch (itemId) {
      case "RAG-LIC-1W":
        price = String.valueOf(UIController.LICENSE_COST_ONE_WEEK);
        break;
      case "RAG-LIC-1M":
        price = String.valueOf(UIController.LICENSE_COST_ONE_MONTH);
        break;
      default:
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown itemId: " + itemId);
    }

    String orderId = payPalService.createOrder(itemName, itemId, price, "USD");
    return Map.of("id", orderId);
  }

  @PostMapping("/orders/{orderId}/capture")
  public Map<String, Object> capture(@PathVariable String orderId) {
    return payPalService.captureOrder(orderId);
  }

}
