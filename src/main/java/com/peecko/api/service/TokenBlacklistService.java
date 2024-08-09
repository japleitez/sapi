package com.peecko.api.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    private static final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long TIME_TO_LIVE = 1000 * 60 * 60L;

    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(this::removeExpiredEntries, 1, 5, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    public void invalidateToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            long expirationTime = System.currentTimeMillis() + TIME_TO_LIVE;
            blacklist.put(token, expirationTime);
        }
    }

    public boolean isInvalid(String token) {
        return blacklist.containsKey(token);
    }

    public void removeExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        for (String key : blacklist.keySet()) {
            Long expirationTime = blacklist.get(key);
            if (expirationTime != null && expirationTime < currentTime) {
                blacklist.remove(key);
            }
        }
    }

}
