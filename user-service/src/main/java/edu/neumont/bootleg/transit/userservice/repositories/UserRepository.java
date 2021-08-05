package edu.neumont.bootleg.transit.userservice.repositories;


import edu.neumont.bootleg.transit.userservice.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {



}
