package org.example.monitoringservice.factory;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.repository.DbReadingRepository;
import org.example.monitoringservice.repository.ReadingRepository;
import org.example.monitoringservice.service.ReadingService;
import org.example.monitoringservice.service.ReadingServiceImpl;
/**
 * A factory implementation for creating reading-related components.
 */
@RequiredArgsConstructor
public class ReadingComponentFactoryImpl implements ReadingComponentFactory {
    private final String url;
    private final String userName;
    private final String password;
    @Override
    public ReadingController createReadingController() {
        return new ReadingController(createReadingService());
    }

    @Override
    public ReadingService createReadingService() {
        return new ReadingServiceImpl(createReadingRepository());
    }

    @Override
    public ReadingRepository createReadingRepository() {
        return new DbReadingRepository(url, userName, password);
    }
}
