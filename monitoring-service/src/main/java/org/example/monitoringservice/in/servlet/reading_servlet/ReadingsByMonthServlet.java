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
import org.example.monitoringservice.exception.custom.ParameterMissingException;
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
 * A servlet for retrieving readings by month.
 * Handles the HTTP GET request for retrieving readings by month.
 * Initializes the ObjectMapper with custom configuration.
 * Initializes the ReadingComponentFactory using the application properties.
 */
@WebServlet("/reading/month")
public class ReadingsByMonthServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private ReadingComponentFactory readingComponentFactory;

    /**
     * Constructs a new ReadingsByMonthServlet and initializes the
     * ObjectMapper with custom configuration.
     */
    public ReadingsByMonthServlet() {
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
     * Handles the HTTP GET request to retrieve readings by month.
     * Checks if the user is authenticated, if not, throws a NotAuthenticatedException.
     * Retrieves the "monthNum" parameter from the request and checks if it is present and not empty.
     * If the "monthNum" parameter is missing or empty, throws a ParameterMissingException.
     * Calls the ReadingController to get the readings by month and serializes the response to JSON.
     * Sends the JSON response using ServletResponseUtil.
     * Catches and handles NotAuthenticatedException and ParameterMissingException by sending appropriate error responses.
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
            String monthNum = req.getParameter("monthNum");
            if (monthNum == null || monthNum.isEmpty()) {
                throw new ParameterMissingException("Parameter 'monthNum' is required");
            }
            ResponseEntity<List<ReadingResponse>> readingsByMonth =
                    readingComponentFactory.createReadingController()
                            .getReadingsByMonth(monthNum);
            String responseJson = objectMapper.writeValueAsString(readingsByMonth);
            ServletResponseUtil.formOkResponse(resp, responseJson);

        } catch (NotAuthenticatedException | ParameterMissingException e) {
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
