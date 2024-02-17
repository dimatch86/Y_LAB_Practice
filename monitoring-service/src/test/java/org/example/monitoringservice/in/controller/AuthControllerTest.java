package org.example.monitoringservice.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.example.monitoringservice.exception.GlobalExceptionHandler;
import org.example.monitoringservice.exception.custom.UserAlreadyExistException;
import org.example.monitoringservice.exception.custom.UserNotFoundException;
import org.example.monitoringservice.mapper.mapstruct.UserMapper;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.service.AuthenticationServiceImpl;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
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
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationServiceImpl authenticationService;
    @InjectMocks
    private AuthController authController;
    private static final String EMAIL = "admin@mail.ru";


    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @AfterEach
    public void tearDown() {
        UserContext.setCurrentUser(null);
    }

    @Test
    void testRegistration_WhenPostWithValidRequestBody_ThenReturnsOk() throws Exception {
        UserDto userDto = new UserDto("admin@mail.ru", "userPassword", "USER");
        String json = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testRegistration_WhenPostWithInvalidRequestBody_ThenReturnsBadRequest() throws Exception {
        UserDto userDto = new UserDto("admin@mail.ru", "", "USER");
        String json = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistration_WhenUserAlreadyExists_ThenReturnsBadRequest() throws Exception {
        UserDto userDto = new UserDto("admin@mail.ru", "adminpassword", "USER");
        String json = objectMapper.writeValueAsString(userDto);
        doThrow(UserAlreadyExistException.class).when(authenticationService).registerUser(any());
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_WhenPostWithValidRequestBody_ThenReturnsOk() throws Exception {

        UUID personalAccount = UUID.randomUUID();
        Instant regDate = Instant.now();
        LoginRequestDto loginRequestDto = new LoginRequestDto("admin@mail.ru", "userPassword");
        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, regDate);
        UserResponseDto userResponse = new UserResponseDto("admin@mail.ru", String.valueOf(personalAccount), regDate.toString());
        String json = objectMapper.writeValueAsString(loginRequestDto);

        when(authenticationService.login(loginRequestDto)).thenReturn(user);
        when(userMapper.userToUserResponse(user)).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(EMAIL));
        verify(authenticationService, times(1)).login(loginRequestDto);
    }

    @Test
    void testLogin_WhenPostWithInvalidRequestBody_ThenReturnsBadRequest() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("", "userPassword");
        String json = objectMapper.writeValueAsString(loginRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(status().isBadRequest())
                .andDo(print());
        verify(authenticationService, times(0)).login(loginRequestDto);
    }

    @Test
    void testLogin_WhenUserNotFound_ThenReturnsNotFound() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("someanother@mail.ru", "userPassword");
        String json = objectMapper.writeValueAsString(loginRequestDto);
        when(authenticationService.login(loginRequestDto))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotFoundException))
                .andExpect(status().isNotFound())
                .andDo(print());
        verify(authenticationService, times(1)).login(loginRequestDto);
    }

    @Test
    void testGetUserInfo_WhenAuthenticatedUser_ThenReturnsOk() throws Exception {
        UUID personalAccount = UUID.randomUUID();
        Instant regDate = Instant.now();
        User currentUser = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, regDate);
        UserResponseDto userResponse = new UserResponseDto("admin@mail.ru", String.valueOf(personalAccount), regDate.toString());
        UserContext.setCurrentUser(currentUser);

        when(userMapper.userToUserResponse(currentUser)).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(EMAIL));
    }

    @Test
    void testGetUserInfo_WhenNotAuthenticatedUser_ThenReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
