package org.example.monitoringservice.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.exception.GlobalExceptionHandler;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.exception.custom.NotAvailableReadingException;
import org.example.monitoringservice.exception.custom.ParameterMissingException;
import org.example.monitoringservice.exception.custom.TooRecentReadingException;
import org.example.monitoringservice.mapper.mapstruct.ReadingMapper;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.service.ReadingServiceImpl;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ReadingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private ReadingMapper readingMapper;
    @Mock
    private ReadingServiceImpl readingService;
    @InjectMocks
    private ReadingController readingController;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(readingController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @AfterEach
    public void tearDown() {
        UserContext.setCurrentUser(null);
    }

    @Test
    void testSendReading_WhenRequestWithValidBody_ThenReturnsOk() throws Exception {
        ReadingDto readingDto = new ReadingDto("45.26", "ГРЯЧАЯ ВОДА" );
        String json = objectMapper.writeValueAsString(readingDto);
        UserContext.setCurrentUser(new User());
        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
        verify(readingService, times(1)).send(any());
    }

    @Test
    void testSendReading_WhenRequestWithInvalidBody_ThenReturnsBadRequest() throws Exception {
        ReadingDto readingDto = new ReadingDto("45.26", "" );
        String json = objectMapper.writeValueAsString(readingDto);
        UserContext.setCurrentUser(new User());
        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(readingService, times(0)).send(any());
    }

    @Test
    void testSendReading_WhenUserNotAuthorized_ThenReturnsUnauthorized() throws Exception {
        ReadingDto readingDto = new ReadingDto("45.26", "ГРЯЧАЯ ВОДА" );
        String json = objectMapper.writeValueAsString(readingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof NotAuthenticatedException));
        verify(readingService, times(0)).send(any());
    }

    @Test
    void testSendReading_WhenReadingIsTooRecent_ThenReturnsBadRequest() throws Exception {
        ReadingDto readingDto = new ReadingDto("45.26", "ГРЯЧАЯ ВОДА" );
        String json = objectMapper.writeValueAsString(readingDto);
        doThrow(TooRecentReadingException.class).when(readingService).send(any());
        UserContext.setCurrentUser(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof TooRecentReadingException));
        verify(readingService, times(1)).send(any());
    }

    @Test
    void testSendReading_WhenReadingIsNotAvailable_ThenReturnsNotFound() throws Exception {
        ReadingDto readingDto = new ReadingDto("45.26", "ГРЯЧАЯ ВОДА" );
        String json = objectMapper.writeValueAsString(readingDto);
        doThrow(NotAvailableReadingException.class).when(readingService).send(any());
        UserContext.setCurrentUser(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof NotAvailableReadingException));
        verify(readingService, times(1)).send(any());
    }

    @Test
    void testActualReadings_whenUserAuthorized_thenReturnsOkWithData() throws Exception {
        Reading actualReading = new Reading(12.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", Instant.now());
        List<Reading> readings = List.of(actualReading);
        when(readingService.getActualReadings()).thenReturn(readings);
        when(readingMapper.readingListToResponseList(readings))
                .thenReturn(List.of(new ReadingResponse(actualReading.getReadingValue(),
                        actualReading.getPersonalAccount(), actualReading.getReadingType(),
                        actualReading.getSendingDate().toString())));
        UserContext.setCurrentUser(new User());
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/actual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
        verify(readingService, times(1)).getActualReadings();
    }

    @Test
    void testActualReadings_whenUserNotAuthorized_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/actual"))
                .andExpect(status().isUnauthorized());
        verify(readingService, times(0)).getActualReadings();
    }

    @Test
    void testReadingsByMonth_whenUserAuthorizedAndCorrectParameter_thenReturnsOkWithData() throws Exception {
        Reading actualReading = new Reading(12.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", Instant.now());
        List<Reading> readings = List.of(actualReading);
        when(readingService.getReadingsByMonth(anyInt())).thenReturn(readings);
        when(readingMapper.readingListToResponseList(readings))
                .thenReturn(List.of(new ReadingResponse(actualReading.getReadingValue(),
                        actualReading.getPersonalAccount(), actualReading.getReadingType(),
                        actualReading.getSendingDate().toString())));
        UserContext.setCurrentUser(new User());
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/month")
                        .param("monthNumber", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
        verify(readingService, times(1)).getReadingsByMonth(anyInt());
    }

    @Test
    void testReadingsByMonth_whenUserAuthorizedAndMissingParameter_thenReturnsBadRequest() throws Exception {

        UserContext.setCurrentUser(new User());
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/month")
                        .param("monthNumber", ""))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ParameterMissingException));
        verify(readingService, times(0)).getReadingsByMonth(anyInt());
    }

    @Test
    void testReadingsByMonth_whenUserNotAuthorized_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/month")
                        .param("monthNumber", "2"))
                .andExpect(status().isUnauthorized());
        verify(readingService, times(0)).getHistoryOfReadings();
    }

    @Test
    void testHistoryReadings_whenUserAuthorized_thenReturnsOkWithData() throws Exception {
        Reading actualReading = new Reading(12.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", Instant.now());
        List<Reading> readings = List.of(actualReading);
        when(readingService.getHistoryOfReadings()).thenReturn(readings);
        when(readingMapper.readingListToResponseList(readings))
                .thenReturn(List.of(new ReadingResponse(actualReading.getReadingValue(),
                        actualReading.getPersonalAccount(), actualReading.getReadingType(),
                        actualReading.getSendingDate().toString())));
        UserContext.setCurrentUser(new User());
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
        verify(readingService, times(1)).getHistoryOfReadings();
    }

    @Test
    void testHistoryReadings_whenUserNotAuthorized_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/history"))
                .andExpect(status().isUnauthorized());
        verify(readingService, times(0)).getHistoryOfReadings();
    }
}
