package org.example.monitoringservice.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.model.BlacklistToken;
import org.example.monitoringservice.repository.BlackListTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class BlackListServiceImpl implements BlackListService {

    private final BlackListTokenRepository blackListTokenRepository;

    @Value("${jwt.secret}")
    private String secret;
    @Override
    public void pushTokenToBlacklist(String token) {
        if (token != null) {
            Date expiration = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                    .getBody().getExpiration();
            BlacklistToken blacklistToken = BlacklistToken.builder()
                    .token(token)
                    .expiredAt(expiration)
                    .build();

            blackListTokenRepository.saveToken(blacklistToken);
        }
    }

    @Override
    public boolean isInvalid(String token) {
        return blackListTokenRepository.existsByToken(token);
    }

    @Scheduled(cron = "${scheduled-tasks.blacklist}")
    public void deleteExpiredTokens() {
        blackListTokenRepository.deleteExpiredTokens();
    }
}
