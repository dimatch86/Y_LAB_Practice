package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.model.reading.Reading;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
/**
 * An interface that serves as a mapper for converting data transfer objects (DTO) to
 * domain objects and vice versa.
 * Apply decorators from ReadingMapperDelegate for additional mapping behaviors.
 */
@Mapper
@DecoratedWith(ReadingMapperDelegate.class)
public interface ReadingMapper {
    ReadingMapper READING_MAPPER = Mappers.getMapper(ReadingMapper.class);

    /**
     * Converts a ReadingDto object to a Reading object.
     * @param readingDto the input ReadingDto object
     * @return a corresponding Reading object
     */
    Reading readingDtoToReading(ReadingDto readingDto);

    /**
     * Converts a list of Reading objects to a list of ReadingResponse objects.
     * @param readingList the input list of Reading objects
     * @return a corresponding list of ReadingResponse objects
     */
    List<ReadingResponse> readingListToResponseLIst(List<Reading> readingList);
}
