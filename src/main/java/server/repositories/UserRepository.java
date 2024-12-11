package server.repositories;

import hotelapp.models.User;

/**
 * Interface for userRepository
 * **/
public interface UserRepository {
    /**
     * Finds a user by userName.
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * Saves a new user.
     * @param user
     * @return
     */
    boolean save(User user);

    /**
     * Updates a user
     * @param user
     * @return
     */
    boolean update(User user);
}
