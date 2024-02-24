package org.example.monitoringservice.in.controller;

import org.example.monitoringservice.exception.custom.UserAlreadyExistException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер аутентификации")

class AuthControllerTest extends AbstractTest {

    @Test
    @DisplayName("Получение информации о текущем пользователе")
    @WithMockUser(username = "user@mail.ru")
    void testGetUserInfo_WhenAuthenticatedUser_ThenReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@mail.ru"));
    }

    @Test
    @DisplayName("Регистрация с валидными данными -> ОК")
    @WithMockUser
    void testRegistration_WhenPostWithValidRequestBody_ThenReturnsOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "new@mail.ru",
                          "password": "testtest",
                          "role": "USER"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Регистрация с невалидными данными -> BAD REQUEST")
    @WithMockUser
    void testRegistration_WhenPostWithInvalidRequestBody_ThenReturnsBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "adminmail.ru",
                          "password": "adminapassword",
                          "role": "ROLE_USER"
                        }
                    """))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Регистрация существующего пользователя -> BAD REQUEST")
    @WithMockUser
    void testRegistration_WhenUserAlreadyExists_ThenReturnsBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "admin@mail.ru",
                          "password": "adminapassword",
                          "role": "ADMIN"
                        }
                    """))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Логин существующего пользователя с валидными данными -> ОК")
    @WithMockUser
    void testLogin_WhenPostWithValidRequestBody_ThenReturnsOk() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "user@mail.ru",
                          "password": "shima444"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@mail.ru"));
    }

    @Test
    @DisplayName("Логин существующего пользователя с невалидными данными -> BAD REQUEST")
    @WithMockUser
    void testLogin_WhenPostWithInvalidRequestBody_ThenBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "",
                          "password": "userpassword"
                        }
                    """))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Логин существующего пользователя с невалидными паролем -> UNAUTHORIZED")
    @WithMockUser
    void testLogin_WhenPostWithInvalidPassword_ThenUnauthorized() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "user@mail.ru",
                          "password": "userpass"
                        }
                    """))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
