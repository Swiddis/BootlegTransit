package edu.neumont.bootleg.transit.notificationsservice.controllers;

import edu.neumont.bootleg.transit.notificationsservice.models.Notification;
import edu.neumont.bootleg.transit.notificationsservice.repositories.NotificationsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationsRestController {

    private final NotificationsRepository repo;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Notification> getNotification(@PathVariable int id) {
        return repo.findById(id);
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification Notification) {
        return repo.save(Notification);
    }

    @PatchMapping("/{id}")
    public Notification patchNotification(@PathVariable int id, @RequestBody Notification from) {
        Optional<Notification> to = repo.findById(id);
        if (to.isPresent()) {

            Notification notif = to.get();

            if (from.getTitle() != null)
                notif.setTitle(from.getTitle());

            if (from.getBody() != null)
                notif.setBody(from.getBody());

            if (from.getActiveUtil() != null)
                notif.setActiveUtil(from.getActiveUtil());

            repo.save(notif);
            return notif;
        }

        //TODO Probably actually throw an error so we can return a different response.
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable int id) {
        repo.deleteById(id);
    }

}
