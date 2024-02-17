package org.example.monitoringservice.in.servlet.reading_servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.factory.ReadingComponentFactory;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class SendReadingServletTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ReadingComponentFactory readingComponentFactory;
    private ReadingController readingController;
    private SendReadingServlet sendReadingServlet;
    private PrintWriter writer;
    User user;

    @BeforeEach
    void setup() {

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        readingController = mock(ReadingController.class);
        readingComponentFactory = mock(ReadingComponentFactory.class);
        writer = mock(PrintWriter.class);
        user = new User(UUID.randomUUID(), "diman@mail.ru",
                BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        UserContext.setCurrentUser(user);
        sendReadingServlet = new SendReadingServlet();
        sendReadingServlet.setReadingComponentFactory(readingComponentFactory);
    }

    @Test
    void testDoPost_whenUserAuthenticatedAndValidInput_thenReturnsOk() throws IOException {

        when(readingComponentFactory.createReadingController()).thenReturn(readingController);

        String jsonContent = "{\"value\":45.25,\"type\": \"ГОРЯЧАЯ ВОДА\"}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonContent));

        when(request.getReader()).thenReturn(bufferedReader);

        when(response.getWriter()).thenReturn(writer);
        sendReadingServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
    }

    @Test
    void testDoPost_whenUserAuthenticatedAndInvalidInput_thenReturnsBadRequest() throws IOException {

        when(readingComponentFactory.createReadingController()).thenReturn(readingController);

        String jsonContent = "{\"type\": \"ГОРЯЧАЯ ВОДА\"}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonContent));

        when(request.getReader()).thenReturn(bufferedReader);

        when(response.getWriter()).thenReturn(writer);
        sendReadingServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }

    @Test
    void testDoPost_whenUserNotAuthenticated_thenReturnsBadRequest() throws IOException {
        UserContext.setCurrentUser(null);

        when(readingComponentFactory.createReadingController()).thenReturn(readingController);

        String jsonContent = "{\"value\":45.25,\"type\": \"ГОРЯЧАЯ ВОДА\"}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonContent));

        when(request.getReader()).thenReturn(bufferedReader);

        when(response.getWriter()).thenReturn(writer);
        sendReadingServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }
}
