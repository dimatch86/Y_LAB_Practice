package org.example.monitoringservice.in.servlet.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.factory.AuditComponentFactory;
import org.example.monitoringservice.in.controller.AuditController;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AuditServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuditComponentFactory auditComponentFactory;
    private AuditController auditController;
    private AuditServlet auditServlet;
    private PrintWriter writer;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        auditController = mock(AuditController.class);
        auditComponentFactory = mock(AuditComponentFactory.class);
        writer = mock(PrintWriter.class);
        User user = new User(UUID.randomUUID(), "diman@mail.ru",
                BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.ADMIN, Instant.now());
        UserContext.setCurrentUser(user);
        auditServlet = new AuditServlet();
        auditServlet.setAuditComponentFactory(auditComponentFactory);
    }

    @Test
    void testDoGet_whenUserAuthenticatedAndUserIsAdmin_thenReturnsOk() throws IOException {

        when(auditComponentFactory.createAuditController()).thenReturn(auditController);
        when(response.getWriter()).thenReturn(writer);

        auditServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(response).getWriter();
    }

    @Test
    void testDoGet_whenUserNotAuthenticated_thenReturnsBadRequest() throws IOException {

        when(auditComponentFactory.createAuditController()).thenReturn(auditController);
        when(response.getWriter()).thenReturn(writer);
        UserContext.setCurrentUser(null);

        auditServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(auditController);
    }

    @Test
    void testDoGet_whenUserAuthenticatedAndUserIsNotAdmin_thenReturnsBadRequest() throws IOException {

        when(auditComponentFactory.createAuditController()).thenReturn(auditController);
        when(response.getWriter()).thenReturn(writer);
        UserContext.getCurrentUser().setRole(RoleType.USER);

        auditServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response.getWriter()).write(anyString());
        verifyNoInteractions(auditController);
    }
}
