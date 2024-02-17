package org.example.monitoringservice.service;

import org.example.monitoringservice.model.audit.Action;

import java.util.List;
/**
 * The AuditService interface provides methods for saving and retrieving audit actions.
 */
public interface AuditService {

    /**
     * Saves the given action into the audit log.
     * @param action The Action object to be saved.
     */
    void saveAction(Action action);

    /**
     * Retrieves a list of all audit actions from the audit log.
     * @return A list of Action objects representing the audit actions.
     */
    List<Action> getUsersActions();
}
