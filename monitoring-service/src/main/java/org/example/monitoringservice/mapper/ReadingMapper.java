package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.model.reading.Reading;

public class ReadingMapper {

    public Reading readingDtoToReading(ReadingDto readingDto) {
        return Reading.builder()
                .value(readingDto.getValue())
                .personalAccount(readingDto.getPersonalAccount())
                .readingType(readingDto.getType())
                .build();
    }
}
