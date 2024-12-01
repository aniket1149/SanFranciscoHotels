package server.repositories;

import hotelapp.models.User;
import jakarta.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepositoryImpl.class);
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try(Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String password = rs.getString("hashed_password");
                String salt = rs.getString("salt");
                return new User(username, password, salt);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users (username, hashed_password, salt) VALUES (?, ?, ?)";
        try(Connection conn = DatabaseUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getHashedPassword());
            ps.setString(3, user.getSalt());
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        } catch (SQLException e) {
            logger.error(e);
        }
        return false;
    }
}
