package edu.neumont.bootleg.transit.notificationsservice.repositories;


import edu.neumont.bootleg.transit.notificationsservice.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationsRepository extends MongoRepository<Notification, Integer> {
    List<Notification> findAllByActiveUntilGreaterThan(LocalDateTime time);
}
