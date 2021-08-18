package edu.neumont.bootleg.transit.notificationsservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Document
@Data
public class Notification {
    @Id
    public Long id = new SecureRandom().nextLong();

    public String title, body;

    public LocalDateTime activeUntil;

    public boolean isActive() {
        return activeUntil.isAfter(LocalDateTime.now());
    }
}
