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
import static org.mockito.Mockito.verify;

class RegistrationServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthComponentFactory authComponentFactory;
    private AuthController authController;
    private RegistrationServlet registrationServlet;
    private PrintWriter writer;
    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authController = mock(AuthController.class);
        authComponentFactory = mock(AuthComponentFactory.class);
        writer = mock(PrintWriter.class);
        registrationServlet = new RegistrationServlet();
        registrationServlet.setAuthComponentFactory(authComponentFactory);
    }

    @Test
    void testDoPost_whenPostWithValidData_thenReturnsOk() throws IOException {

        String validRequestJson =
                "{\"email\":\"user@mail.ru\",\"password\":\"pass\",\"role\":\"USER\"}";
        BufferedReader reader = new BufferedReader(new StringReader(validRequestJson));
        when(authComponentFactory.createAuthController()).thenReturn(authController);
        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);

        registrationServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
    }

    @Test
    void testDoPost_whenPostWithInvalidData_thenReturnsBadRequest() throws IOException {

        String validRequestJson =
                "{\"email\":\"user@mail.ru\",\"role\":\"USER\"}";
        BufferedReader reader = new BufferedReader(new StringReader(validRequestJson));
        when(authComponentFactory.createAuthController()).thenReturn(authController);
        when(request.getReader()).thenReturn(reader);
        when(response.getWriter()).thenReturn(writer);

        registrationServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(authController);
    }
}