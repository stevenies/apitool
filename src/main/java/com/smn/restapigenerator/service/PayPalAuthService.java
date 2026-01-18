package com.smn.restapigenerator.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class PayPalAuthService {

  private final RestClient rest;
  private final String baseUrl;
  private final String clientId;
  private final String secret;

  private volatile String token;
  private volatile Instant expiresAt = Instant.EPOCH;

  public PayPalAuthService(RestClient.Builder builder,
                           @Value("${paypal.base-url}") String baseUrl,
                           @Value("${paypal.client-id}") String clientId,
                           @Value("${paypal.secret}") String secret) {
    this.rest = builder.build();
    this.baseUrl = baseUrl;
    this.clientId = clientId;
    this.secret = secret;
  }

  public synchronized String getAccessToken() {
    if (token != null && Instant.now().isBefore(expiresAt.minusSeconds(30))) {
      return token;
    }

    String basic = Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "client_credentials");

    Map<?, ?> resp = rest.post()
        .uri(baseUrl + "/v1/oauth2/token")
        .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(Map.class);

    token = (String) resp.get("access_token");
    Number expiresIn = (Number) resp.get("expires_in");
    expiresAt = Instant.now().plusSeconds(expiresIn.longValue());
    return token;
  }
}
