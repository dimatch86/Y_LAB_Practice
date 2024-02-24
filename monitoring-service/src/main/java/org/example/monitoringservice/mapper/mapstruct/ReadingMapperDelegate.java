package org.example.monitoringservice.mapper.mapstruct;

import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.ReadingResponseDto;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.util.DateConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public Reading readingDtoToReading(ReadingDto readingDto, UUID personalAccount) {
        return Reading.builder()
                .readingValue(Double.parseDouble(readingDto.getValue()))
                .personalAccount(personalAccount)
                .readingType(readingDto.getType().toUpperCase())
                .build();
    }

    /**
     * Converts a Reading object to a ReadingResponse object with formatted sending date.
     * @param reading the input Reading object
     * @return a corresponding ReadingResponse object with formatted sending date
     */
    private ReadingResponseDto readingToReadingResponse(Reading reading) {
        return ReadingResponseDto.builder()
                .value(reading.getReadingValue())
                .readingType(reading.getReadingType())
                .personalAccount(reading.getPersonalAccount())
                .sendingDate(DateConverter.formatDate(reading.getSendingDate()))
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
    public List<ReadingResponseDto> readingListToResponseList(List<Reading> readingList) {
        if (readingList == null) {
            return new ArrayList<>();
        }
        return readingList.stream()
                .map(this::readingToReadingResponse)
                .toList();
    }
}
