package org.example.monitoringservice.in.controller;

import org.example.monitoringservice.exception.custom.NotAvailableReadingException;
import org.example.monitoringservice.exception.custom.ParameterMissingException;
import org.example.monitoringservice.exception.custom.TooRecentReadingException;
import org.example.monitoringservice.model.reading.Reading;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@DisplayName("Контроллер показаний")
class ReadingControllerTest extends AbstractTest {


    @Test
    @DisplayName("Передача показаний с валидными данными -> ОК")
    @WithUserDetails(value = "user@mail.ru")
    void testSendReading_WhenRequestWithValidBody_ThenReturnsOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "value": "25.25",
                          "type": "ГОРЯЧАЯ ВОДА"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Передача показаний с невалидными данными -> BAD_REQUEST")
    @WithUserDetails(value = "user@mail.ru")
    void testSendReading_WhenRequestWithInvalidBody_ThenReturnsBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "value": "25.25",
                          "type": ""
                        }
                    """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
    }

    @Test
    @DisplayName("Передача показаний неавторизованным пользователем -> UNAUTHORIZED")
    void testSendReading_WhenUserNotAuthorized_ThenReturnsUnauthorized() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "value": "25.25",
                          "type": "ГОРЯЧАЯ ВОДА"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Передача показаний чаще чем раз в месяц -> BAD_REQUEST")
    @WithUserDetails(value = "user@mail.ru")
    void testSendReading_WhenReadingIsTooRecent_ThenReturnsBadRequest() throws Exception {

        readingService.send(new Reading(25.25, UUID.fromString("9e597713-8725-4579-b3df-f26ee1f58497"), "ГОРЯЧАЯ ВОДА", Instant.now()));

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "value": "25.25",
                          "type": "ГОРЯЧАЯ ВОДА"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof TooRecentReadingException));
    }

    @Test
    @DisplayName("Передача показаний с неподдерживаемым типом -> NOT_FOUND")
    @WithUserDetails(value = "user@mail.ru")
    void testSendReading_WhenReadingIsNotAvailable_ThenReturnsNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/reading/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "value": "25.25",
                          "type": "ГАЗ"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof NotAvailableReadingException));
    }

    @Test
    @DisplayName("Запрос актуальных показаний авторизованным пользователем -> ОК")
    @WithUserDetails(value = "user@mail.ru")
    void testActualReadings_whenUserAuthorized_thenReturnsOkWithData() throws Exception {
        Reading actualReading = new Reading(12.25, UUID.fromString("9e597713-8725-4579-b3df-f26ee1f58497"), "ГОРЯЧАЯ ВОДА", Instant.now());
        readingService.send(actualReading);
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/actual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("Запрос актуальных показаний неавторизованным пользователем -> UNAUTHORIZED")
    void testActualReadings_whenUserNotAuthorized_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/actual"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Запрос показаний за месяц авторизованным пользователем -> ОК")
    @WithUserDetails(value = "user@mail.ru")
    void testReadingsByMonth_whenUserAuthorizedAndCorrectParameter_thenReturnsOkWithData() throws Exception {
        Reading actualReading = new Reading(12.25, UUID.fromString("9e597713-8725-4579-b3df-f26ee1f58497"), "ГОРЯЧАЯ ВОДА", Instant.now());
        readingService.send(actualReading);

        mockMvc.perform(MockMvcRequestBuilders.get("/reading/month")
                        .param("monthNumber", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("Запрос показаний за месяц без параметра месяца -> BAD_REQUEST")
    @WithUserDetails(value = "user@mail.ru")
    void testReadingsByMonth_whenUserAuthorizedAndMissingParameter_thenReturnsBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/reading/month")
                        .param("monthNumber", ""))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof ParameterMissingException));
    }

    @Test
    @DisplayName("Запрос показаний за месяц неавторизованным пользователем -> UNAUTHORIZED")
    void testReadingsByMonth_whenUserNotAuthorized_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/month")
                        .param("monthNumber", "2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Запрос истории показаний авторизованным пользователем -> ОК")
    @WithUserDetails(value = "user@mail.ru")
    void testHistoryReadings_whenUserAuthorized_thenReturnsOkWithData() throws Exception {
        Reading reading = new Reading(12.25, UUID.fromString("9e597713-8725-4579-b3df-f26ee1f58497"), "ГОРЯЧАЯ ВОДА", Instant.now());
        readingService.send(reading);
        mockMvc.perform(MockMvcRequestBuilders.get("/reading/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}
