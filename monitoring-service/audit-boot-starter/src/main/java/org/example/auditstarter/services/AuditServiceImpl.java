package org.example.auditstarter.services;



import lombok.RequiredArgsConstructor;
import org.example.auditstarter.model.Action;
import org.example.auditstarter.repository.ActionRepository;
import org.springframework.stereotype.Service;

/**
 * The AuditServiceImpl class implements the AuditService interface
 * and provides methods for saving and retrieving audit actions.
 */
@Service
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
}
