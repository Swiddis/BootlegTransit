package edu.neumont.bootleg.transit.userservice.controllers;

import edu.neumont.bootleg.transit.userservice.models.User;
import edu.neumont.bootleg.transit.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserRestController {

    private final UserRepository repo;

    @GetMapping
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @GetMapping("/{username}")
    public Optional<User> getUser(@PathVariable String username) {
        return repo.findById(username);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return repo.save(user);
    }

    @PatchMapping("/{username}")
    public User patchUser(@PathVariable String username, @RequestBody User from) {
        Optional<User> to = repo.findById(username);
        if (to.isPresent()) {

            User us = to.get();

            if (from.getEmail() != null)
                us.setEmail(from.getEmail());

            if (from.getPassword() != null)
                us.setPassword(from.getPassword());

            repo.save(us);
            return us;
        }

        //TODO Probably actually throw an error so we can return a different response.
        return null;
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        repo.deleteById(username);
    }
}
