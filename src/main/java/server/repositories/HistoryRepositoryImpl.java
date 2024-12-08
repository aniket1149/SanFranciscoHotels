package server.repositories;

import hotelapp.models.History;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.utils.DatabaseUtil;
import server.utils.PreparedStatements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryRepositoryImpl extends HotelRepositoryImpl implements HistoryRepository {
    private static final Logger logger = LogManager.getLogger(HistoryRepositoryImpl.class);
    private DatabaseUtil databaseUtil = DatabaseUtil.getInstance();
    public HistoryRepositoryImpl() {
        databaseUtil.createTable("history");
    }
    @Override
    public boolean saveHistory(History history) {
        String sql = PreparedStatements.INSERT_HISTORY;
        try(Connection conn = databaseUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,String.valueOf(history.getHotelId()));
            ps.setString(2, String.valueOf(history.getUserName()));
            ps.setString(3, history.getTime());
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        }catch (SQLException e){
            logger.error(e);
        }
        return false;
    }

    @Override
    public List<History> findUserClickHistory(String userName) {
        List<History> historyList = new ArrayList<>();
        String sql = PreparedStatements.GET_USER_HISTORY;
        try(Connection conn = databaseUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,userName);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                History history = new History(
                        rs.getString("hotel_id"),
                        rs.getString("user_name"),
                        rs.getString("time"),
                        rs.getString("name"),
                        rs.getString("link")
                );
                historyList.add(history);
            }
        }catch (SQLException e){
            logger.error("Error getting all history", e);
        }
        return historyList;
    }

    @Override
    public boolean updateHistory(History history, String username) {
        String sql = PreparedStatements.UPDATE_HISTORY;
        try(Connection conn = databaseUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, history.getTime());
            ps.setString(2, history.getHotelId());
            ps.setString(3, username);
            return ps.executeUpdate()>0;

        }catch (SQLException e){
            logger.error("Error while updating history", e);
        }
        return false;
    }
}
