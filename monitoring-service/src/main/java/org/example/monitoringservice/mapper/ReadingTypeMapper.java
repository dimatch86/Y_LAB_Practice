package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.request.ReadingTypeDto;
import org.example.monitoringservice.model.reading.ReadingType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
/**
 * An interface for mapping reading type data transfer objects (DTO) to reading type
 * domain objects and vice versa.
 * The READING_TYPE_MAPPER provides a singleton instance of the ReadingTypeMapper.
 */
@Mapper
public interface ReadingTypeMapper {

    /**
     * Singleton instance of the ReadingTypeMapper for mapping reading type DTOs
     * to domain objects and vice versa.
     */
    ReadingTypeMapper READING_TYPE_MAPPER = Mappers.getMapper(ReadingTypeMapper.class);

    /**
     * Converts a ReadingTypeDto object to a ReadingType object.
     * @param readingTypeDto the input ReadingTypeDto object
     * @return a corresponding ReadingType object
     */
    ReadingType readingTypeDtoToReadingType(ReadingTypeDto readingTypeDto);

}
