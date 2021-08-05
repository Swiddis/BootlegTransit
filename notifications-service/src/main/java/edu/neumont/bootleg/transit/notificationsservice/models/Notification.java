package edu.neumont.bootleg.transit.notificationsservice.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

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
