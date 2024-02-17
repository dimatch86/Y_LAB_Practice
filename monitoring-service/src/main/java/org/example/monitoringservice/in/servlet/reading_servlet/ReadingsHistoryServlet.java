package org.example.monitoringservice.in.servlet.reading_servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
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
 * A servlet for retrieving readings history.
 * Handles the HTTP GET request for retrieving readings history.
 * Initializes the ObjectMapper with custom configuration.
 * Initializes the ReadingComponentFactory using the application properties.
 */
@WebServlet("/reading/history")
public class ReadingsHistoryServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private ReadingComponentFactory readingComponentFactory;

    /**
     * Constructs a new ReadingsHistoryServlet and initializes the
     * ObjectMapper with custom configuration.
     */
    public ReadingsHistoryServlet() {
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
    public void init() throws ServletException {
        AppProps appProps = AppPropertiesProvider.getProperties();
        readingComponentFactory =
                new ReadingComponentFactoryImpl(appProps.getUrl(), appProps.getUserName(), appProps.getPassword());
    }

    /**
     * Handles the HTTP GET request to retrieve readings history.
     * Checks if the user is authenticated, if not, throws a NotAuthenticatedException.
     * Retrieves the readings history using ReadingComponentFactory and serializes the response to JSON format.
     * Sends the JSON response using ServletResponseUtil.
     * @param req the HttpServletRequest object for the request
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs while handling the request
     * @throws NotAuthenticatedException if the user is not authenticated
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (UserContext.isNotAuthenticated()) {
                throw new NotAuthenticatedException("Вы не авторизованы. Выполните вход в ваш аккаунт");
            }
            ResponseEntity<List<ReadingResponse>> readingsHistory =
                    readingComponentFactory.createReadingController()
                            .getHistoryOfReadings();

            String responseJson = objectMapper.writeValueAsString(readingsHistory);
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

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
