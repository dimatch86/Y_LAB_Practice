package org.example.monitoringservice.in.servlet.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.configuration.AppPropertiesProvider;
import org.example.monitoringservice.configuration.AppProps;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.exception.CustomException;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.exception.custom.NotEnoughRightsException;
import org.example.monitoringservice.factory.AuditComponentFactory;
import org.example.monitoringservice.factory.AuditComponentFactoryImpl;
import org.example.monitoringservice.model.audit.Action;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.ServletResponseUtil;
import org.example.monitoringservice.util.UserContext;

import java.io.IOException;
import java.util.List;
/**
 * Servlet for handling audit-related operations.
 */
@WebServlet("/audit")
public class AuditServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private AuditComponentFactory auditComponentFactory;

    /**
     * Constructs a new AuditServlet and initializes the ObjectMapper.
     */
    public AuditServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Initializes the servlet by creating an instance of AuditComponentFactory
     * using the application properties.
     * @throws ServletException if there's an issue initializing the servlet
     */
    @Override
    public void init() throws ServletException {
        AppProps appProps = AppPropertiesProvider.getProperties();
        auditComponentFactory =
                new AuditComponentFactoryImpl(appProps.getUrl(), appProps.getUserName(), appProps.getPassword());
    }

    /**
     * Handles the HTTP GET request for retrieving audit actions.
     * Checks user authentication and admin rights before fetching the audit actions.
     * If the checks fail, appropriate exceptions are thrown.
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (UserContext.isNotAuthenticated()) {
                throw new NotAuthenticatedException("Вы не авторизованы. Выполните вход в ваш аккаунт");
            }
            if (UserContext.isNotAdmin()) {
                throw new NotEnoughRightsException("Недостаточно прав для данного действия");
            }
            ResponseEntity<List<Action>> actions =
                    auditComponentFactory.createAuditController()
                            .auditActions();

            String responseJson = objectMapper.writeValueAsString(actions);
            ServletResponseUtil.formOkResponse(resp, responseJson);

        } catch (CustomException e) {
            ResponseEntity<Object> error = ResponseUtil.errorResponse(e.getLocalizedMessage());
            String errorResponseJson = objectMapper.writeValueAsString(error);
            ServletResponseUtil
                    .formErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, errorResponseJson);
        }
    }

    public void setAuditComponentFactory(AuditComponentFactory auditComponentFactory) {
        this.auditComponentFactory = auditComponentFactory;
    }
}
