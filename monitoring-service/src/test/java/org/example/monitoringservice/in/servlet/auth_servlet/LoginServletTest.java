package org.example.monitoringservice.in.servlet.auth_servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.factory.AuthComponentFactory;
import org.example.monitoringservice.in.controller.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.Mockito.*;

class LoginServletTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthComponentFactory authComponentFactory;
    private AuthController authController;
    private LoginServlet loginServlet;
    private PrintWriter writer;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authController = mock(AuthController.class);
        authComponentFactory = mock(AuthComponentFactory.class);
        writer = mock(PrintWriter.class);
        loginServlet = new LoginServlet();
        loginServlet.setAuthComponentFactory(authComponentFactory);
    }

    @Test
    void testDoPost_whenPostWithValidData_thenReturnsOk() throws IOException {

        String validRequestJson = "{\"email\":\"user@mail.ru\",\"password\":\"pass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(validRequestJson));
        when(authComponentFactory.createAuthController()).thenReturn(authController);
        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
    }

    @Test
    void testDoPost_whenPostWithInvalidData_thenReturnsBadRequest() throws IOException {

        String validRequestJson = "{\"email\":\"user@mail.ru\",\"password\":\"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(validRequestJson));
        when(authComponentFactory.createAuthController()).thenReturn(authController);
        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(authController);
    }
}
