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

class AddReadingTypeServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ReadingComponentFactory readingComponentFactory;
    private ReadingController readingController;
    private AddReadingTypeServlet addReadingTypeServlet;
    private PrintWriter writer;
    @BeforeEach
    void setup() {

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        readingController = mock(ReadingController.class);
        readingComponentFactory = mock(ReadingComponentFactory.class);
        writer = mock(PrintWriter.class);
        User user = new User(UUID.randomUUID(), "diman@mail.ru",
                BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        UserContext.setCurrentUser(user);
        addReadingTypeServlet = new AddReadingTypeServlet();
        addReadingTypeServlet.setReadingComponentFactory(readingComponentFactory);
    }

    @Test
    void testDoPost_whenUserAuthenticatedAndUserIsAdmin_thenReturnsOk() throws IOException {

        UserContext.getCurrentUser().setRole(RoleType.ADMIN);
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);

        String jsonContent = "{\"type\": \"ГАЗ\"}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonContent));

        when(request.getReader()).thenReturn(bufferedReader);

        when(response.getWriter()).thenReturn(writer);
        addReadingTypeServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
    }

    @Test
    void testDoPost_whenInvalidDto_thenReturnsBadRequest() throws IOException {

        UserContext.getCurrentUser().setRole(RoleType.ADMIN);
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);

        String jsonContent = "{\"type\": \"\"}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonContent));

        when(request.getReader()).thenReturn(bufferedReader);

        when(response.getWriter()).thenReturn(writer);
        addReadingTypeServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }

    @Test
    void testDoPost_whenNotAuthenticated_thenReturnsBadRequest() throws IOException {

        UserContext.setCurrentUser(null);
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);

        String jsonContent = "{\"type\": \"ГАЗ\"}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonContent));

        when(request.getReader()).thenReturn(bufferedReader);

        when(response.getWriter()).thenReturn(writer);
        addReadingTypeServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }

    @Test
    void testDoPost_whenUserIsNotAdmin_thenReturnsBadRequest() throws IOException {

        when(readingComponentFactory.createReadingController()).thenReturn(readingController);

        String jsonContent = "{\"type\": \"ГАЗ\"}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonContent));

        when(request.getReader()).thenReturn(bufferedReader);

        when(response.getWriter()).thenReturn(writer);
        addReadingTypeServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }
}
