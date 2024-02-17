package org.example.monitoringservice.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateConverter {

    /**
     * Formats the input Instant date to a string representation with
     * the pattern "yyyy-MM-dd HH:mm:ss".
     * @param date the input Instant date
     * @return the formatted date string
     */
    public String formatDate(Instant date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = date.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(formatter);
    }
}
