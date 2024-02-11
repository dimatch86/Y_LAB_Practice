package org.example.monitoringservice.in.servlet.reading_servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.factory.ReadingComponentFactory;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ReadingsByMonthServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ReadingComponentFactory readingComponentFactory;
    private ReadingController readingController;
    private ReadingsByMonthServlet readingsByMonthServlet;
    private ObjectMapper objectMapper;
    private PrintWriter writer;
    User user;
    @BeforeEach
    void setup() {

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        readingController = mock(ReadingController.class);
        readingComponentFactory = mock(ReadingComponentFactory.class);
        writer = mock(PrintWriter.class);
        objectMapper = mock(ObjectMapper.class);
        user = new User(UUID.randomUUID(), "diman@mail.ru",
                BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        UserContext.setCurrentUser(user);
        readingsByMonthServlet = new ReadingsByMonthServlet();
        readingsByMonthServlet.setReadingComponentFactory(readingComponentFactory);
    }

    @Test
    void testDoGet_whenUserAuthenticatedAndMonthParameterIsPresent_thenReturnsOk() throws IOException {

        List<ReadingResponse> readingList = new ArrayList<>();
        ResponseEntity<List<ReadingResponse>> readingsByMonth = ResponseUtil.okResponseWithData((readingList));

        when(request.getParameter("monthNum")).thenReturn("1");
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);
        when(readingComponentFactory.createReadingController().getReadingsByMonth("1")).thenReturn(readingsByMonth);

        when(objectMapper.writeValueAsString(readingsByMonth)).thenReturn("mockResponseJson");

        readingsByMonthServlet.setObjectMapper(objectMapper);

        when(response.getWriter()).thenReturn(writer);
        readingsByMonthServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
        verify(writer).write("mockResponseJson");
    }

    @Test
    void testDoGet_whenUserIsNotAuthenticated_thenReturnsBadRequest() throws IOException {
        List<ReadingResponse> readingList = new ArrayList<>();
        ResponseEntity<List<ReadingResponse>> readingsByMonth = ResponseUtil.okResponseWithData((readingList));
        when(readingController.getActualReadings()).thenReturn(readingsByMonth);
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);
        when(readingComponentFactory.createReadingController().getActualReadings()).thenReturn(readingsByMonth);

        when(response.getWriter()).thenReturn(writer);
        UserContext.setCurrentUser(null);

        readingsByMonthServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }

    @Test
    void testDoGet_whenUserAuthenticatedAndMonthParameterIsNotPresent_thenReturnsBadRequest() throws IOException {

        List<ReadingResponse> readingList = new ArrayList<>();
        ResponseEntity<List<ReadingResponse>> readingsByMonth = ResponseUtil.okResponseWithData((readingList));

        when(request.getParameter("monthNum")).thenReturn("");
        when(readingComponentFactory.createReadingController()).thenReturn(readingController);
        when(readingComponentFactory.createReadingController().getReadingsByMonth("1")).thenReturn(readingsByMonth);

        when(response.getWriter()).thenReturn(writer);
        readingsByMonthServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(readingController);
    }
}
