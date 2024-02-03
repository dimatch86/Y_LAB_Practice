package org.example.monitoringservice.factory;

import org.example.monitoringservice.configuration.AppConfig;
import org.example.monitoringservice.in.controller.ReadingController;
import org.example.monitoringservice.repository.DbReadingRepository;
import org.example.monitoringservice.repository.ReadingRepository;
import org.example.monitoringservice.service.ReadingService;
import org.example.monitoringservice.service.ReadingServiceImpl;

public class ReadingComponentFactoryImpl extends AppConfig implements ReadingComponentFactory {
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
        return new DbReadingRepository(getUrl(), getUserName(), getPassword());
    }
}
