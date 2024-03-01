package org.example.monitoringservice.in.controller;

import org.example.monitoringservice.exception.custom.ReadingTypeAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер типов показаний")
class ReadingTypeControllerTest extends AbstractTest {

    @Test
    @DisplayName("Добавление нового типа авторизованным пользователем -> ОК")
    @WithUserDetails(value = "admin@mail.ru")
    void addNewReadingTypeTest_whenUserAuthenticatedAndUserIsAdmin_thenReturnsOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "type": "ГАЗ"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Добавление нового типа неавторизованным пользователем -> UNAUTHORIZED")
    void addNewReadingTypeTest_whenUserNotAuthenticated_thenReturnsUnauthorized() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "type": "ГАЗ"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Добавление уже существующего типа -> BAD_REQUEST")
    @WithUserDetails(value = "admin@mail.ru")
    void addNewReadingTypeTest_whenReadingTypeExists_thenBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "type": "ГОРЯЧАЯ ВОДА"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ReadingTypeAlreadyExistsException));
    }

    @Test
    @DisplayName("Добавление нового типа с невалидными данными -> BAD_REQUEST")
    @WithUserDetails(value = "admin@mail.ru")
    void addNewReadingTypeTest_whenInvalidRequestData_thenBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "type": ""
                        }
                    """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
    }
}
