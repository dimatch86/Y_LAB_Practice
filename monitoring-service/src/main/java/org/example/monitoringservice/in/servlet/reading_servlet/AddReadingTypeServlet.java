package org.example.monitoringservice.in.servlet.reading_servlet;

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
import org.example.monitoringservice.dto.request.ReadingTypeDto;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.exception.CustomException;
import org.example.monitoringservice.exception.custom.InvalidDataException;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.exception.custom.NotEnoughRightsException;
import org.example.monitoringservice.factory.ReadingComponentFactory;
import org.example.monitoringservice.factory.ReadingComponentFactoryImpl;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.ServletResponseUtil;
import org.example.monitoringservice.util.UserContext;
import org.example.monitoringservice.util.ValidationUtil;

import java.io.IOException;
/**
 * A servlet for adding a new reading type.
 */
@WebServlet("/reading/add")
public class AddReadingTypeServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private ReadingComponentFactory readingComponentFactory;

    /**
     * Constructs a new AddReadingTypeServlet and initializes the
     * ObjectMapper with custom configuration.
     */
    public AddReadingTypeServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Initializes the servlet by creating an instance of ReadingComponentFactory
     * using the application properties.
     */
    @Override
    public void init() {
        AppProps appProps = AppPropertiesProvider.getProperties();
        readingComponentFactory =
                new ReadingComponentFactoryImpl(appProps.getUrl(), appProps.getUserName(), appProps.getPassword());
    }

    /**
     * A servlet for adding a new reading type.
     * Handles the HTTP POST request for adding a new reading type.
     * If the user is not authenticated, a NotAuthenticatedException is thrown.
     * If the user is not an admin, a NotEnoughRightsException is thrown.
     * Reads the request body and validates the input data.
     * Performs the operation to add a new reading type and sends the response.
     * If a NotAuthenticatedException, NotEnoughRightsException or InvalidDataException occurs during the operation, an error response is sent.
     * @param req the HttpServletRequest object for the request
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (UserContext.isNotAuthenticated()) {
                throw new NotAuthenticatedException("Вы не авторизованы. Выполните вход в ваш аккаунт");
            }
            if (UserContext.isNotAdmin()) {
                throw new NotEnoughRightsException("Недостаточно прав для данного действия");
            }

            ReadingTypeDto readingType = objectMapper.readValue(req.getReader(), ReadingTypeDto.class);
            String errs = ValidationUtil.validate(readingType);
            if (!errs.isEmpty()) {
                throw new InvalidDataException(errs);
            }
            ResponseEntity<Object> response = readingComponentFactory.createReadingController()
                    .addNewReadingType(readingType);
            String responseJson = objectMapper.writeValueAsString(response);
            ServletResponseUtil.formOkResponse(resp, responseJson);

        } catch (CustomException e) {
            ResponseEntity<Object> error = ResponseUtil.errorResponse(e.getLocalizedMessage());
            String errorResponseJson = objectMapper.writeValueAsString(error);
            ServletResponseUtil
                    .formErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, errorResponseJson);
        }
    }

    public void setReadingComponentFactory(ReadingComponentFactory readingComponentFactory) {
        this.readingComponentFactory = readingComponentFactory;
    }
}
