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
import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.exception.CustomException;
import org.example.monitoringservice.exception.custom.InvalidDataException;
import org.example.monitoringservice.factory.AuthComponentFactory;
import org.example.monitoringservice.factory.AuthComponentFactoryImpl;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.ServletResponseUtil;
import org.example.monitoringservice.util.ValidationUtil;

import java.io.IOException;
/**
 * A servlet for handling user registration operations.
 */
@WebServlet("/auth/register")
public class RegistrationServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private AuthComponentFactory authComponentFactory;

    /**
     * Constructs a new RegistrationServlet and initializes the ObjectMapper.
     */
    public RegistrationServlet() {
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
        authComponentFactory =
                new AuthComponentFactoryImpl(appProps.getUrl(), appProps.getUserName(), appProps.getPassword());
    }

    /**
     * Handles the HTTP POST request for user registration.
     * Reads the registration request data, validates it, and performs the user registration operation.
     * If the request data is invalid, an InvalidDataException is thrown.
     * If a CustomException occurs during the registration operation, an error response is sent.
     * @param req the HttpServletRequest object containing the registration request data
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDto userDto = objectMapper.readValue(req.getReader(), UserDto.class);

            String errs = ValidationUtil.validate(userDto);
            if (!errs.isEmpty()) {
                throw new InvalidDataException(errs);
            }
            ResponseEntity<Object> response = authComponentFactory.createAuthController()
                    .registerNewUser(userDto);
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
