package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.model.audit.Action;
import org.example.monitoringservice.repository.ActionRepository;

import java.util.List;
/**
 * The AuditServiceImpl class implements the AuditService interface
 * and provides methods for saving and retrieving audit actions.
 */
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final ActionRepository actionRepository;

    /**
     * Saves the given action into the audit log.
     * @param action The Action object to be saved.
     */
    @Override
    public void saveAction(Action action) {
        actionRepository.save(action);

    }

    /**
     * Retrieves a list of all audit actions from the audit log.
     * @return A list of Action objects representing the audit actions.
     */
    @Override
    public List<Action> auditActions() {
        return actionRepository.findAllActions();
    }
}
