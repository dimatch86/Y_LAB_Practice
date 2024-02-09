package org.example.monitoringservice.factory;

import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.repository.ReadingRepository;
import org.example.monitoringservice.service.ReadingService;

public interface ReadingComponentFactory {

    ReadingController createReadingController();
    ReadingService createReadingService();
    ReadingRepository createReadingRepository();
}
