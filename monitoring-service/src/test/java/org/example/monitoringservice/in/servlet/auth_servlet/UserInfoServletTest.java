package org.example.monitoringservice.in.servlet.auth_servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.factory.AuthComponentFactory;
import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class UserInfoServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthComponentFactory authComponentFactory;
    private AuthController authController;
    private UserInfoServlet userInfoServlet;
    private PrintWriter writer;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authController = mock(AuthController.class);
        authComponentFactory = mock(AuthComponentFactory.class);
        writer = mock(PrintWriter.class);
        User user = new User(UUID.randomUUID(), "diman@mail.ru",
                BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        UserContext.setCurrentUser(user);
        userInfoServlet = new UserInfoServlet();
        userInfoServlet.setAuthComponentFactory(authComponentFactory);
    }

    @Test
    void testDoGet_whenUserAuthenticated_thenReturnsOk() throws IOException {

        when(authComponentFactory.createAuthController()).thenReturn(authController);
        when(response.getWriter()).thenReturn(writer);

        userInfoServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
    }

    @Test
    void testDoGet_whenUserNotAuthenticated_thenReturnsBadRequest() throws IOException {

        when(authComponentFactory.createAuthController()).thenReturn(authController);
        when(response.getWriter()).thenReturn(writer);
        UserContext.setCurrentUser(null);

        userInfoServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(authController);
    }
}
