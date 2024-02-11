package org.example.monitoringservice.in.servlet.reading_servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.configuration.AppPropertiesProvider;
import org.example.monitoringservice.configuration.AppProps;
import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.exception.CustomException;
import org.example.monitoringservice.exception.custom.InvalidDataException;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.factory.ReadingComponentFactory;
import org.example.monitoringservice.factory.ReadingComponentFactoryImpl;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.ServletResponseUtil;
import org.example.monitoringservice.util.UserContext;
import org.example.monitoringservice.util.ValidationUtil;

import java.io.IOException;
/**
 * A servlet for sending readings to the server.
 * Initializes the ObjectMapper with custom configuration.
 * Initializes the ReadingComponentFactory using the application properties.
 */
@WebServlet("/reading/send")
public class SendReadingServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private ReadingComponentFactory readingComponentFactory;

    /**
     * Constructs a new SendReadingServlet and initializes the
     * ObjectMapper with custom configuration.
     */
    public SendReadingServlet() {
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
     * Handles the HTTP POST request to send a reading to the server.
     * Checks if the user is authenticated, if not, throws a NotAuthenticatedException.
     * Reads the input from the request's reader and deserializes it into a ReadingDto object using the ObjectMapper.
     * Sends the reading data to the server using ReadingComponentFactory and retrieves the response.
     * Serializes the response to JSON format and sends it back as the HTTP response.
     * @param req the HttpServletRequest object for the request
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs while handling the request
     * @throws NotAuthenticatedException if the user is not authenticated
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (UserContext.isNotAuthenticated()) {
                throw new NotAuthenticatedException("Вы не авторизованы. Выполните вход в ваш аккаунт");
            }
            ReadingDto readingDto = objectMapper.readValue(req.getReader(), ReadingDto.class);
            String errs = ValidationUtil.validate(readingDto);
            if (!errs.isEmpty()) {
                throw new InvalidDataException(errs);
            }
            ResponseEntity<Object> response = readingComponentFactory.createReadingController()
                    .sendReading(readingDto);
            String responseJson = objectMapper.writeValueAsString(response);
            ServletResponseUtil.formOkResponse(resp, responseJson);

        } catch (CustomException | InvalidFormatException e) {
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
