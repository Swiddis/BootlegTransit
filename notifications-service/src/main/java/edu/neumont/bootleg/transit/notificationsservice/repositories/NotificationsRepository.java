package edu.neumont.bootleg.transit.notificationsservice.repositories;


import edu.neumont.bootleg.transit.notificationsservice.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationsRepository extends MongoRepository<Notification, Integer> {


}
