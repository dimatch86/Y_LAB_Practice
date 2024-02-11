package org.example.monitoringservice.factory;

import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.repository.ReadingRepository;
import org.example.monitoringservice.service.ReadingService;
/**
 * A factory interface for creating reading-related components.
 */
public interface ReadingComponentFactory {
    /**
     * Creates an instance of the ReadingController.
     * @return the created ReadingController
     */
    ReadingController createReadingController();
    /**
     * Creates an instance of the ReadingService.
     * @return the created ReadingService
     */
    ReadingService createReadingService();
    /**
     * Creates an instance of the ReadingRepository.
     * @return the created ReadingRepository
     */
    ReadingRepository createReadingRepository();
}
