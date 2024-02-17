package org.example.monitoringservice.factory;

import org.example.monitoringservice.in.controller.AuditController;
import org.example.monitoringservice.repository.ActionRepository;
import org.example.monitoringservice.service.AuditService;
/**
 * A factory interface for creating audit-related components.
 */
public interface AuditComponentFactory {

    /**
     * Creates an instance of the AuditController.
     * @return the created AuditController
     */
    AuditController createAuditController();
    /**
     * Creates an instance of the AuditService.
     * @return the created AuditService
     */
    AuditService createAuditService();
    /**
     * Creates an instance of the ActionRepository.
     * @return the created ActionRepository
     */
    ActionRepository createActionRepository();
}
