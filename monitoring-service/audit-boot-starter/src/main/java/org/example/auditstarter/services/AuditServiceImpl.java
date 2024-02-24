package org.example.auditstarter.services;



import org.example.auditstarter.model.Action;
import org.example.auditstarter.repository.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The AuditServiceImpl class implements the AuditService interface
 * and provides methods for saving and retrieving audit actions.
 */
@Service
public class AuditServiceImpl implements AuditService {

    private final ActionRepository actionRepository;

    @Autowired
    public AuditServiceImpl(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    /**
     * Saves the given action into the audit log.
     * @param action The Action object to be saved.
     */
    @Override
    public void saveAction(Action action) {
        actionRepository.save(action);

    }
}
