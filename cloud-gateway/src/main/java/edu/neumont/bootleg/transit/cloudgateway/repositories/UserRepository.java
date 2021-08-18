package edu.neumont.bootleg.transit.cloudgateway.repositories;

import edu.neumont.bootleg.transit.cloudgateway.models.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
