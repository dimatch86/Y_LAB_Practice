package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.audit.Action;

import java.util.List;
/**
 * This interface represents a repository for storing and retrieving Action objects.
 */
public interface ActionRepository {

    /**
     * Saves the given Action object to the repository.
     * @param action The Action object to be saved.
     */
    void save(Action action);
    /**
     * Retrieves all the Action objects stored in the repository.
     * @return A list of all the Action objects.
     */
    List<Action> findAllActions();
}
