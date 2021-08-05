package edu.neumont.bootleg.transit.notificationsservice.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class Notification {

    @Id
    @Getter
    public int id;

    @Getter
    @Setter
    public String title, body;

    @Getter
    @Setter
    public LocalDateTime activeUtil;

    public boolean isActive() {
        return activeUtil.isAfter(LocalDateTime.now());
    }

}
