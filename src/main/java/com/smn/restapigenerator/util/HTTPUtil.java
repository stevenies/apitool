package com.smn.restapigenerator.util;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class HTTPUtil {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HTTPUtil.class);

    /**
     * Makes an HTTP POST request to the specified URL with the given payload.
     * @param url the target URL
     * @param payload the request body as a string
     * @return the response body as a string
     * @throws Exception if the request fails
     */
    public static String makeHttpPostCall(String url, String payload) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            
            logger.debug("HTTP POST to {} returned status: {}", url, response.getStatusCode());
            return response.getBody();
            
        } catch (Exception e) {
            logger.error("HTTP POST request failed to {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

}