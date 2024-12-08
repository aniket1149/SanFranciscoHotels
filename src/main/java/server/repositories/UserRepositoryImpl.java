package server.repositories;

import hotelapp.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.utils.DatabaseUtil;
import server.utils.PreparedStatements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation class for UserRepository
 * @implSpec UserRepository
 * **/
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepositoryImpl.class);
    private DatabaseUtil databaseUtil = DatabaseUtil.getInstance();

    public UserRepositoryImpl() {
        databaseUtil.createTable("users");
    }

    /**
     * Searches User by username
     * @param username
     * @return User
     */
    @Override
    public User findByUsername(String username) {
        String sql = PreparedStatements.GET_USER;
        try(Connection conn = databaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String password = rs.getString("hashed_password");
                String salt = rs.getString("salt");
                String id = rs.getString("id");
                String lastLogin = rs.getString("last_login");
                return new User(id, username, password, salt, lastLogin);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    /**
     * inserts the new user data to the database.
     * @param user : User Class requried,
     * **/
    @Override
    public boolean save(User user) {
        String sql = PreparedStatements.INSERT_USER;
        try(Connection conn = databaseUtil.getConnection()){
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

    @Override
    public boolean update(User user) {
        String sql = PreparedStatements.UPDATE_LAST_LOGIN;
        try(Connection conn = databaseUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getLastLogin());
            ps.setString(2, user.getUsername());
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        } catch (SQLException e) {
            logger.error(e);
        }
        return false;
    }
}
