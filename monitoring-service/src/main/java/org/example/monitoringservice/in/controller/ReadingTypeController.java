package org.example.monitoringservice.in.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.ReadingTypeDto;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.exception.custom.NotEnoughRightsException;
import org.example.monitoringservice.in.controller.swagger.SwaggerReadingTypeController;
import org.example.monitoringservice.mapper.mapstruct.ReadingTypeMapper;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.service.ReadingTypeService;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reading-type")
@RequiredArgsConstructor
public class ReadingTypeController implements SwaggerReadingTypeController {

    private final ReadingTypeMapper readingTypeMapper;
    private final ReadingTypeService readingTypeService;

    /**
     * Endpoint for adding a new reading type.
     * @param readingTypeDto the information of the reading type to be added
     * @return a ResponseEntity with a success message
     */
    @PostMapping("/add")
    @Override
    public ResponseEntity<ResponseDto<?>> addNewReadingType(@RequestBody @Valid ReadingTypeDto readingTypeDto) {
        if (UserContext.isNotAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        if (UserContext.isNotAdmin()) {
            throw new NotEnoughRightsException("Недостаточно прав для данного действия");
        }
        ReadingType readingType = readingTypeMapper
                .readingTypeDtoToReadingType(readingTypeDto);
        readingTypeService.addNewReadingType(readingType);
        return ResponseEntity.ok(ResponseUtil.okResponse("Новый тип показаний сохранен"));
    }
}
