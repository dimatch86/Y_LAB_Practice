package org.example.auditstarter.services;


import org.example.auditstarter.model.Action;

/**
 * The AuditService interface provides methods for saving and retrieving audit actions.
 */
public interface AuditService {

    /**
     * Saves the given action into the audit log.
     * @param action The Action object to be saved.
     */
    void saveAction(Action action);
}
