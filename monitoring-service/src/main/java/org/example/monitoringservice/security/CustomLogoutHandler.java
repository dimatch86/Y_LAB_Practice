package org.example.monitoringservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.service.BlackListService;
import org.example.monitoringservice.util.TokenGetter;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BlackListService blackListService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = TokenGetter.getToken(request);
        blackListService.pushTokenToBlacklist(token);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_OK);
        body.put("message", "Logout success");
        body.put("path", request.getServletPath());

        try (PrintWriter writer = response.getWriter()) {
            objectMapper.writeValue(writer, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
