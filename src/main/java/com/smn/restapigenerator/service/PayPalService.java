package com.smn.restapigenerator.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PayPalService {

  private final RestClient rest;
  private final PayPalAuthService auth;
  private final String baseUrl;

  public PayPalService(RestClient.Builder builder,
                       PayPalAuthService auth,
                       @Value("${paypal.base-url}") String baseUrl) {
    this.rest = builder.build();
    this.auth = auth;
    this.baseUrl = baseUrl;
  }

  public String createOrder(String itemName, String itemId, String price, String currency) {
    String token = auth.getAccessToken();

    Map<String, Object> payload = Map.of(
        "intent", "CAPTURE",
        "purchase_units", List.of(
            Map.of(
                "custom_id", itemId,           // store your itemId here
                "description", itemName,
                "amount", Map.of(
                    "currency_code", currency,
                    "value", price,
                    "breakdown", Map.of(
                        "item_total", Map.of("currency_code", currency, "value", price)
                    )
                ),
                "items", List.of(
                    Map.of(
                        "name", itemName,
                        "sku", itemId,
                        "quantity", "1",
                        "unit_amount", Map.of("currency_code", currency, "value", price)
                    )
                )
            )
        )
    );

    // Orders v2: create order :contentReference[oaicite:4]{index=4}
    Map<?, ?> resp = rest.post()
        .uri(baseUrl + "/v2/checkout/orders")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .retrieve()
        .body(Map.class);

    return (String) resp.get("id");
  }

    @SuppressWarnings("unchecked")
    public Map<String, Object> captureOrder(String orderId) {
    String token = auth.getAccessToken();

    // Orders v2: capture order :contentReference[oaicite:5]{index=5}
    return rest.post()
        .uri(baseUrl + "/v2/checkout/orders/" + orderId + "/capture")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .body(Map.class);
  }

}
