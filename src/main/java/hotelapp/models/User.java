package hotelapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String hashedPassword;
    private String salt;
    private String lastLogin;

    public User(String username, String hashedPassword, String salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public User(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.hashedPassword = user.getHashedPassword();
        this.salt = user.getSalt();
        this.lastLogin = user.getLastLogin();
    }
}
