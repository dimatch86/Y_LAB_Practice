package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.model.audit.Action;
import org.example.monitoringservice.service.AuditService;
import org.example.monitoringservice.util.ResponseUtil;

import java.util.List;
/**
 * Controller for handling audit-related operations.
 */
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * Endpoint for retrieving audit actions.
     * @return a ResponseEntity containing the list of audit actions, or an error response
     */
    public ResponseEntity<List<Action>> auditActions() {
        return ResponseUtil.okResponseWithData(auditService.auditActions());
    }
}
