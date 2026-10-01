package com.campusconnect.campusconnect.config;

import com.campusconnect.campusconnect.repository.LostFoundItemRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ExpiredLostFoundCleanup {
    private final LostFoundItemRepository items;
    public ExpiredLostFoundCleanup(LostFoundItemRepository items){this.items=items;}
    @Scheduled(cron="0 0 3 * * *")
    public void removeExpired(){items.deleteByExpiresAtBefore(LocalDateTime.now());}
}
