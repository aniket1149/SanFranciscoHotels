package hotelapp.models;

import lombok.Data;

@Data
public class User {
    private String username;
    private String hashedPassword;
    private String salt;

    public User(String username, String hashedPassword, String salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

}
