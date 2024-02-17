package org.example.monitoringservice.in.servlet.reading_servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.factory.ReadingComponentFactory;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ActualReadingsServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ReadingComponentFactory readingComponentFactory;
    private ReadingController readingController;
    private ActualReadingsServlet actualReadingsServlet;
    private PrintWriter writer;
    private User user;

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
        actualReadingsServlet = new ActualReadingsServlet();
        actualReadingsServlet.setReadingComponentFactory(readingComponentFactory);
    }

    @Test
    void testDoGet_whenUserAuthenticated_thenReturnsOk() throws IOException {
        ResponseEntity<List<ReadingResponse>> responseEntity = new ResponseEntity<>();
        when(readingController.getActualReadings()).thenReturn(responseEntity);
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);
        when(readingComponentFactory.createReadingController().getActualReadings()).thenReturn(responseEntity);

        when(response.getWriter()).thenReturn(writer);

        actualReadingsServlet.doGet(request, response);

        verify(readingComponentFactory, times(2)).createReadingController();
        verify(response.getWriter()).write(anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGet_whenUserIsNotAuthenticated_thenReturnsBadRequest() throws IOException {
        ResponseEntity<List<ReadingResponse>> responseEntity = new ResponseEntity<>();
        when(readingController.getActualReadings()).thenReturn(responseEntity);
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);
        when(readingComponentFactory.createReadingController().getActualReadings()).thenReturn(responseEntity);

        when(response.getWriter()).thenReturn(writer);
        UserContext.setCurrentUser(null);

        actualReadingsServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }

    @Test
    void testDoGet_whenAuthenticatedAndActualReadingsAvailable_thenReturnsData() throws IOException {
        UUID personalAccount = UUID.fromString("80bd02d7-bddb-4c9d-826e-c54b53dd475b");

        List<ReadingResponse> actualReadings = List.of(
                new ReadingResponse(25.5, personalAccount, "ГОРЯЧАЯ ВОДА", "some_date"),
                new ReadingResponse(45.15, personalAccount, "ХОЛОДНАЯ ВОДА", "some_date"));

        ResponseEntity<List<ReadingResponse>> responseEntity = new ResponseEntity<>(actualReadings, "", "date");

        Mockito.lenient().when(readingComponentFactory.createReadingController())
                .thenReturn(readingController);
        Mockito.lenient().when(readingController.getActualReadings()).thenReturn(responseEntity);

        when(response.getWriter()).thenReturn(writer);

        actualReadingsServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String expectedJson = "{\"data\":[{\"value\":25.5,\"personalAccount\":\"80bd02d7-bddb-4c9d-826e-c54b53dd475b\",\"readingType\":\"ГОРЯЧАЯ ВОДА\",\"sendingDate\":\"some_date\"},{\"value\":45.15,\"personalAccount\":\"80bd02d7-bddb-4c9d-826e-c54b53dd475b\",\"readingType\":\"ХОЛОДНАЯ ВОДА\",\"sendingDate\":\"some_date\"}],\"error\":\"\",\"date\":\"date\"}";
        verify(response.getWriter()).write(expectedJson);
    }
}
