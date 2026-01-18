package com.smn.restapigenerator.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;

import com.smn.restapigenerator.ui.UIController;
import com.smn.restapigenerator.util.MapUtil;

@Service
public class PayPalService {

    private final RestClient rest;
    private final PayPalAuthService auth;
    private final String productionBaseUrl;
    private final String sandboxBaseUrl;

	private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    public PayPalService(
        RestClient.Builder builder,
        PayPalAuthService auth,
        @Value("${paypal.base-url}") String productionBaseUrl,
        @Value("${paypal.sandbox-base-url}") String sandboxBaseUrl) {

        this.rest = builder.build();
        this.auth = auth;
        this.productionBaseUrl = productionBaseUrl;
        this.sandboxBaseUrl = sandboxBaseUrl;
    }

    public String createOrder(String itemName, String itemId, String price, String currency, boolean useSandbox) {
        String token = auth.getAccessToken(useSandbox);
        String baseUrl = useSandbox ? sandboxBaseUrl : productionBaseUrl;

        Map<String, Object> payload = Map.of(
            "intent", "CAPTURE",
            "purchase_units", List.of(
                Map.of(
                    "custom_id", itemId,
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
        RequestBodySpec request = rest.post()
            .uri(baseUrl + "/v2/checkout/orders")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload);
        Map<?, ?> response = request.retrieve().body(Map.class);
        String orderId = (String) response.get("id");
        return orderId;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> captureOrder(String orderId, boolean useSandbox) {
        String token = auth.getAccessToken(useSandbox);
        String baseUrl = useSandbox ? sandboxBaseUrl : productionBaseUrl;

        // Orders v2: capture order :contentReference[oaicite:5]{index=5}
        try {
            RequestBodySpec request = rest.post()
                .uri(baseUrl + "/v2/checkout/orders/{orderId}/capture", orderId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(Collections.emptyMap());
            Map<String, Object> response = request.retrieve().body(Map.class);
            System.out.println(MapUtil.dumpMap(response));
            return response;
        } catch (Throwable t) {
            logger.error("Failed to capture PayPal order: " + orderId, t);
            throw new RuntimeException("Failed to capture PayPal order: " + orderId, t);
        }
    }

}
