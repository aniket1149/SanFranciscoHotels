package server.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.models.GeoTags;
import hotelapp.models.HotelDTO;
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

public class HotelRepositoryImpl implements HotelRepository {
    private static final Logger logger = LogManager.getLogger(HotelRepositoryImpl.class);
    private DatabaseUtil databaseUtil = DatabaseUtil.getInstance();

    public HotelRepositoryImpl() {
        databaseUtil.createTable("hotels");
    }

    @Override
    public HotelDTO findHotelById(String hotelId) {
        String sql = PreparedStatements.GET_HOTEL;
        try(Connection conn = databaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hotelId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                HotelDTO h = new HotelDTO();
                h.setId(rs.getString("id"));
                h.setName(rs.getString("name"));
                h.setCity(rs.getString("city"));
                h.setStreetAddress(rs.getString("address"));
                h.setState(rs.getString("state"));
                h.setLl(new GeoTags(rs.getString("Longitude"), rs.getString("Latitude")));
                h.setLink(rs.getString("link"));
                h.setRating(rs.getString("ratings"));
                return h;
            }
        }catch (SQLException e){
            logger.error(e);
        }
        return null;
    }

    @Override
    public List<HotelDTO> findHotelByName(String name) {
        List<HotelDTO> hotels = new ArrayList<>();
        String sql = PreparedStatements.GET_HOTEL_BYNAME;
        try(Connection conn = databaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%"+name+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                HotelDTO h = new HotelDTO();
                h.setId(rs.getString("id"));
                h.setName(rs.getString("name"));
                h.setCity(rs.getString("city"));
                h.setStreetAddress(rs.getString("address"));
                h.setState(rs.getString("state"));
                h.setLl(new GeoTags(rs.getString("Longitude"), rs.getString("Latitude")));
                h.setRating(rs.getString("ratings"));
                hotels.add(h);
            }
        }catch (SQLException e){
            logger.error(e);
        }
        return hotels;
    }

    @Override
    public boolean save(HotelDTO hotel) {
        String sql = PreparedStatements.INSERT_HOTEL;
        try(Connection conn = databaseUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hotel.getId());
            ps.setString(2, hotel.getName());
            ps.setString(3, hotel.getStreetAddress());
            ps.setString(4, hotel.getCity());
            ps.setString(5, hotel.getLat());
            ps.setString(6, hotel.getLng());
            ps.setString(7, hotel.getState());
            ps.setString(8, hotel.getLink());
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        } catch (SQLException e) {
            logger.error(e);
        }
        return false;
    }

    @Override
    public List<JsonObject> fetchLatLang() {

        List<JsonObject> res = new ArrayList<>();
        String sql = PreparedStatements.GET_LAT_LNG;
        try(Connection conn = databaseUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                JsonObject o = new JsonObject();
                o.addProperty("name", rs.getString("name"));
                o.addProperty("long", rs.getString("longitude"));
                o.addProperty("lat", rs.getString("latitude"));
                res.add(o);
            }
        }catch (SQLException e){
            logger.error("Error getting lat lng", e);
        }
        return res;
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        List<HotelDTO> hotels = new ArrayList<>();
        String sql = PreparedStatements.GET_HOTELS;
        try(Connection conn = databaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                HotelDTO h = new HotelDTO();
                h.setId(rs.getString("id"));
                h.setName(rs.getString("name"));
                h.setCity(rs.getString("city"));
                h.setStreetAddress(rs.getString("address"));
                h.setState(rs.getString("state"));
                h.setLl(new GeoTags(rs.getString("Longitude"), rs.getString("Latitude")));
                h.setRating(rs.getString("ratings"));
                hotels.add(h);
            }
        }catch (SQLException e){
            logger.error(e);
        }
        return hotels;
    }
//To be done in part 2 when Reviews will be stored in db
}
