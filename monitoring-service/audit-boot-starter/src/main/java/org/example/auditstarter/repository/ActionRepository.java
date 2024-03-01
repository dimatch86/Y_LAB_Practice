package org.example.auditstarter.repository;



import org.example.auditstarter.model.Action;

/**
 * This interface represents a repository for storing and retrieving Action objects.
 */
public interface ActionRepository {

    /**
     * Saves the given Action object to the repository.
     * @param action The Action object to be saved.
     */
    void save(Action action);
}
