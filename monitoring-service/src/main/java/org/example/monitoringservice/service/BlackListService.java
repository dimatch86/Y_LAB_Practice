package org.example.monitoringservice.service;

public interface BlackListService {

    void pushTokenToBlacklist(String token);
    boolean isInvalid(String token);
}
