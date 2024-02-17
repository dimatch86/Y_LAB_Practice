package org.example.monitoringservice.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.monitoringservice.dto.request.ReadingTypeDto;
import org.example.monitoringservice.exception.GlobalExceptionHandler;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.exception.custom.NotEnoughRightsException;
import org.example.monitoringservice.exception.custom.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.mapper.mapstruct.ReadingTypeMapper;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.service.ReadingTypeServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ReadingTypeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private ReadingTypeMapper readingTypeMapper;

    @Mock
    private ReadingTypeServiceImpl readingTypeService;
    @InjectMocks
    private ReadingTypeController readingTypeController;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(readingTypeController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }
    @AfterEach
    public void tearDown() {
        UserContext.setCurrentUser(null);
    }

    @Test
    void addNewReadingTypeTest_whenUserAuthenticatedAndUserIsAdmin_thenReturnsOk() throws Exception {
        ReadingTypeDto readingTypeDto = new ReadingTypeDto("ГАЗ" );
        String json = objectMapper.writeValueAsString(readingTypeDto);
        UserContext.setCurrentUser(new User());
        UserContext.getCurrentUser().setRole(RoleType.ADMIN);
        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
        verify(readingTypeService, times(1)).addNewReadingType(any());
    }
    @Test
    void addNewReadingTypeTest_whenUserAuthenticatedAndUserIsUser_thenReturnsUnauthorized() throws Exception {
        ReadingTypeDto readingTypeDto = new ReadingTypeDto("ГАЗ" );
        String json = objectMapper.writeValueAsString(readingTypeDto);
        UserContext.setCurrentUser(new User());
        UserContext.getCurrentUser().setRole(RoleType.USER);
        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof NotEnoughRightsException));
        verify(readingTypeService, times(0)).addNewReadingType(any());
    }

    @Test
    void addNewReadingTypeTest_whenUserNotAuthenticated_thenReturnsUnauthorized() throws Exception {
        ReadingTypeDto readingTypeDto = new ReadingTypeDto("ГАЗ" );
        String json = objectMapper.writeValueAsString(readingTypeDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof NotAuthenticatedException));
        verify(readingTypeService, times(0)).addNewReadingType(any());
    }

    @Test
    void addNewReadingTypeTest_whenReadingTypeExists_thenBadRequest() throws Exception {
        ReadingTypeDto readingTypeDto = new ReadingTypeDto("ГАЗ" );
        String json = objectMapper.writeValueAsString(readingTypeDto);
        UserContext.setCurrentUser(new User());
        UserContext.getCurrentUser().setRole(RoleType.ADMIN);
        doThrow(ReadingTypeAlreadyExistsException.class).when(readingTypeService).addNewReadingType(any());
        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ReadingTypeAlreadyExistsException));
        verify(readingTypeService, times(1)).addNewReadingType(any());
    }

    @Test
    void addNewReadingTypeTest_whenInvalidRequestData_thenBadRequest() throws Exception {
        ReadingTypeDto readingTypeDto = new ReadingTypeDto("");
        String json = objectMapper.writeValueAsString(readingTypeDto);
        System.out.println(json);
        UserContext.setCurrentUser(new User());
        UserContext.getCurrentUser().setRole(RoleType.ADMIN);
        mockMvc.perform(MockMvcRequestBuilders.post("/reading-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(readingTypeService, times(0)).addNewReadingType(any());
    }
}
