package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.util.UserContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 * An implementation of the ReadingMapper interface that provides additional mapping behaviors.
 * Extends the ReadingMapper interface and implements custom mapping methods.
 */
public abstract class ReadingMapperDelegate implements ReadingMapper {

    /**
     * Converts a ReadingDto object to a Reading object with
     * additional properties set from the UserContext.
     * @param readingDto the input ReadingDto object
     * @return a corresponding Reading object with additional properties set
     */
    public Reading readingDtoToReading(ReadingDto readingDto) {
        return Reading.builder()
                .value(readingDto.getValue())
                .personalAccount(UserContext.getCurrentUser().getPersonalAccount())
                .readingType(readingDto.getType())
                .build();
    }

    /**
     * Converts a Reading object to a ReadingResponse object with formatted sending date.
     * @param reading the input Reading object
     * @return a corresponding ReadingResponse object with formatted sending date
     */
    private ReadingResponse readingToReadingResponse(Reading reading) {
        return ReadingResponse.builder()
                .value(reading.getValue())
                .readingType(reading.getReadingType())
                .personalAccount(reading.getPersonalAccount())
                .sendingDate(formatDate(reading.getSendingDate()))
                .build();
    }

    /**
     * Converts a list of Reading objects to a list of ReadingResponse objects and applies
     * the custom mapping method to each element in the list.
     * If the input readingList is null, an empty list is returned.
     * @param readingList the input list of Reading objects
     * @return a corresponding list of ReadingResponse objects
     */
    @Override
    public List<ReadingResponse> readingListToResponseLIst(List<Reading> readingList) {
        if (readingList == null) {
            return new ArrayList<>();
        }
        return readingList.stream()
                .map(this::readingToReadingResponse)
                .toList();
    }

    /**
     * Formats the input Instant date to a string representation with
     * the pattern "yyyy-MM-dd HH:mm:ss".
     * @param date the input Instant date
     * @return the formatted date string
     */
    private static String formatDate(Instant date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = date.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(formatter);
    }
}
