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
import org.example.monitoringservice.exception.CustomException;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.factory.AuthComponentFactory;
import org.example.monitoringservice.factory.AuthComponentFactoryImpl;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.ServletResponseUtil;
import org.example.monitoringservice.util.UserContext;

import java.io.IOException;
/**
 * A servlet for handling user info operations.
 */
@WebServlet("/auth/info")
public class UserInfoServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private AuthComponentFactory authComponentFactory;

    /**
     * Constructs a new UserInfoServlet and initializes the ObjectMapper.
     */
    public UserInfoServlet() {
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
     * Handles the HTTP GET request for retrieving current user authority.
     * If the user is not authenticated, a NotAuthenticatedException is thrown.
     * Performs the current user authority retrieval operation and sends the response.
     * If a CustomException occurs during the operation, an error response is sent.
     * @param req the HttpServletRequest object for the request
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            if (UserContext.isNotAuthenticated()) {
                throw new NotAuthenticatedException("Вы не авторизованы. Выполните вход в ваш аккаунт");
            }
            ResponseEntity<Object> response = authComponentFactory.createAuthController().currentUserAuthority();
            String responseJson = objectMapper.writeValueAsString(response);
            ServletResponseUtil.formOkResponse(resp, responseJson);

        } catch (CustomException e) {
            ResponseEntity<Object> error = ResponseUtil.errorResponse(e.getLocalizedMessage());
            String errorResponseJson = objectMapper.writeValueAsString(error);
            ServletResponseUtil
                    .formErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, errorResponseJson);
        }
    }

    public void setAuthComponentFactory(AuthComponentFactory authComponentFactory) {
        this.authComponentFactory = authComponentFactory;
    }
}
