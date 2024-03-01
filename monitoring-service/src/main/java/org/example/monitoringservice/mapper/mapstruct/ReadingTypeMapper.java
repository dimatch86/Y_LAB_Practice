package org.example.monitoringservice.mapper.mapstruct;

import org.example.monitoringservice.dto.request.ReadingTypeDto;
import org.example.monitoringservice.model.reading.ReadingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
/**
 * An interface for mapping reading type data transfer objects (DTO) to reading type
 * domain objects and vice versa.
 * The READING_TYPE_MAPPER provides a singleton instance of the ReadingTypeMapper.
 */
@Mapper(componentModel = "spring")
public interface ReadingTypeMapper {

    /**
     * Converts a ReadingTypeDto object to a ReadingType object.
     * @param readingTypeDto the input ReadingTypeDto object
     * @return a corresponding ReadingType object
     */
    @Mapping(target = "type",
            expression = "java(readingTypeDto.getType().toUpperCase())")
    ReadingType readingTypeDtoToReadingType(ReadingTypeDto readingTypeDto);

}
