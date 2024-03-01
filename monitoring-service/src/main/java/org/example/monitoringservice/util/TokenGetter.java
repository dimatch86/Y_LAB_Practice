package org.example.monitoringservice.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

@UtilityClass
public class TokenGetter {

    public String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
