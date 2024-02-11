package org.example.monitoringservice.in.servlet.reading_servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.factory.ReadingComponentFactory;
import org.example.monitoringservice.factory.ReadingComponentFactoryImpl;
import org.example.monitoringservice.configuration.AppPropertiesProvider;
import org.example.monitoringservice.configuration.AppProps;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.ServletResponseUtil;
import org.example.monitoringservice.util.UserContext;

import java.io.IOException;
import java.util.List;
/**
 * A servlet for handling actual readings operations.
 */
@WebServlet("/reading/actual")
public class ActualReadingsServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private ReadingComponentFactory readingComponentFactory;

    /**
     * Constructs a new ActualReadingsServlet and initializes the
     * ObjectMapper with custom configuration.
     */
    public ActualReadingsServlet() {
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
     * Handles the HTTP GET request for retrieving actual readings.
     * If the user is not authenticated, a NotAuthenticatedException is thrown.
     * Performs the actual readings retrieval operation and sends the response.
     * If a NotAuthenticatedException occurs during the operation, an error response is sent.
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
            ResponseEntity<List<ReadingResponse>> actualReadings =
                    readingComponentFactory.createReadingController()
                            .getActualReadings();

            String responseJson = objectMapper.writeValueAsString(actualReadings);
            ServletResponseUtil.formOkResponse(resp, responseJson);

        } catch (NotAuthenticatedException e) {

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
