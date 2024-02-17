package org.example.monitoringservice.factory;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.in.controller.AuditController;
import org.example.monitoringservice.repository.ActionRepository;
import org.example.monitoringservice.repository.ActionRepositoryImpl;
import org.example.monitoringservice.service.AuditService;
import org.example.monitoringservice.service.AuditServiceImpl;
/**
 * A factory implementation for creating audit-related components.
 */
@RequiredArgsConstructor
public class AuditComponentFactoryImpl implements AuditComponentFactory {
    private final String url;
    private final String userName;
    private final String password;

    @Override
    public AuditController createAuditController() {
        return new AuditController(createAuditService());
    }

    @Override
    public AuditService createAuditService() {
        return new AuditServiceImpl(createActionRepository());
    }

    @Override
    public ActionRepository createActionRepository() {
        return new ActionRepositoryImpl(url, userName, password);
    }
}
