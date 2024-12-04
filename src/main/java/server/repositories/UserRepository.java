package server.repositories;

import hotelapp.models.User;

/**
 * Interface for userRepository
 * **/
public interface UserRepository {
    User findByUsername(String username);
    boolean save(User user);
}
