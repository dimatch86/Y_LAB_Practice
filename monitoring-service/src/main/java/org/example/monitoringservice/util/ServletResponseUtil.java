package org.example.monitoringservice.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

import java.io.IOException;
/**
 * Utility class for forming HTTP servlet responses.
 */
@UtilityClass
public class ServletResponseUtil {

    /**
     * Forms an HTTP servlet response with a success status and data.
     *
     * @param resp the HttpServletResponse object
     * @param data the data to be included in the response
     * @throws IOException if an I/O error occurs
     */
    public void formOkResponse(HttpServletResponse resp, String data) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(data);
    }

    /**
     * Forms an HTTP servlet response with an error status and data.
     *
     * @param resp the HttpServletResponse object
     * @param statusCode the status code to be included in the response
     * @param data the data to be included in the response
     * @throws IOException if an I/O error occurs
     */
    public void formErrorResponse(HttpServletResponse resp, Integer statusCode, String data) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.getWriter().write(data);
    }
}
