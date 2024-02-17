package org.example.monitoringservice.in.servlet.auth_servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.configuration.AppPropertiesProvider;
import org.example.monitoringservice.configuration.AppProps;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.factory.AuthComponentFactory;
import org.example.monitoringservice.factory.AuthComponentFactoryImpl;
import org.example.monitoringservice.util.ServletResponseUtil;

import java.io.IOException;
/**
 * A servlet for handling user logout operations.
 */
@WebServlet("/auth/logout")
public class LogoutServlet extends HttpServlet {

    private AuthComponentFactory authComponentFactory;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new LogoutServlet and initializes the ObjectMapper.
     */
    public LogoutServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    /**
     * Initializes the servlet by creating an instance of AuthComponentFactory
     * using the application properties.
     */
    @Override
    public void init() {
        AppProps appProps = AppPropertiesProvider.getProperties();
        authComponentFactory = new AuthComponentFactoryImpl(appProps.getUrl(), appProps.getUserName(), appProps.getPassword());
    }

    /**
     * Handles the HTTP POST request for user logout.
     * Performs the logout operation and sends a response.
     * @param req the HttpServletRequest object
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResponseEntity<Object> response = authComponentFactory.createAuthController()
                .logout();
        String responseJson = objectMapper.writeValueAsString(response);
        ServletResponseUtil.formOkResponse(resp, responseJson);
    }
}
