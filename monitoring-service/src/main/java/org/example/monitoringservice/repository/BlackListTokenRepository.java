package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.BlacklistToken;


public interface BlackListTokenRepository {

    boolean existsByToken(String token);
    void deleteExpiredTokens();
    void saveToken(BlacklistToken blacklistToken);
}
