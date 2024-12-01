package server.repositories;

import hotelapp.models.User;

public interface UserRepository {
    User findByUsername(String username);
    boolean save(User user);
}
