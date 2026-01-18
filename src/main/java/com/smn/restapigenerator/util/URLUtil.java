package com.smn.restapigenerator.util;

import jakarta.servlet.http.HttpServletRequest;

public class URLUtil {
    
	/**
	 * Checks if the HTTP request is from localhost (local machine).
	 * This method checks multiple sources including X-Forwarded-For header,
	 * X-Real-IP header, and the remote address.
	 * 
	 * @param request the HttpServletRequest to check
	 * @return true if the request is from localhost, false otherwise
	 */
	public static boolean isFromLocalhost(HttpServletRequest request) {
		// Get the client IP address, considering proxy headers
		String clientIp = URLUtil.getClientIpAddress(request);
		
		// Check if the IP is localhost
		return "127.0.0.1".equals(clientIp) || 
			   "0:0:0:0:0:0:0:1".equals(clientIp) || 
			   "::1".equals(clientIp) || 
			   "localhost".equalsIgnoreCase(clientIp);
	}
	
	/**
	 * Gets the client IP address from the request, checking proxy headers first.
	 * 
	 * @param request the HttpServletRequest
	 * @return the client IP address
	 */
	public static String getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
			// X-Forwarded-For can contain multiple IPs, get the first one
			return xForwardedFor.split(",")[0].trim();
		}
		
		String xRealIp = request.getHeader("X-Real-IP");
		if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
			return xRealIp;
		}
		
		String xOriginalFor = request.getHeader("X-Original-For");
		if (xOriginalFor != null && !xOriginalFor.isEmpty() && !"unknown".equalsIgnoreCase(xOriginalFor)) {
			return xOriginalFor;
		}
		
		// Fall back to remote address
		return request.getRemoteAddr();
	}

}
