package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.response.ActionResponseDto;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.exception.custom.NotEnoughRightsException;
import org.example.monitoringservice.in.controller.swagger.SwaggerAuditController;
import org.example.monitoringservice.mapper.mapstruct.ActionMapper;
import org.example.monitoringservice.service.AuditService;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.UserContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * Controller for handling audit-related operations.
 */
@RequiredArgsConstructor
@RestController
public class AuditController implements SwaggerAuditController {

    private final AuditService auditService;
    private final ActionMapper actionMapper;

    /**
     * Endpoint for retrieving audit actions.
     * @return a ResponseEntity containing the list of audit actions, or an error response
     */
    @GetMapping(value = "/audits", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<ResponseDto<List<ActionResponseDto>>> getUsersActionsList() {
        if (UserContext.isNotAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        if (UserContext.isNotAdmin()) {
            throw new NotEnoughRightsException("Недостаточно прав для данного действия");
        }
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(
                actionMapper.actionListToActionResponseList(auditService.getUsersActions())));
    }
}
