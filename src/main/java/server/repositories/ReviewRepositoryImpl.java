package server.repositories;

import hotelapp.models.ReviewDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.utils.DatabaseUtil;
import server.utils.PreparedStatements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReviewRepositoryImpl implements ReviewRepository {
    //To be done in part 2 when Reviews will be stored in db
    private static final Logger logger = LogManager.getLogger(HotelRepositoryImpl.class);
    private DatabaseUtil databaseUtil = DatabaseUtil.getInstance();

    public ReviewRepositoryImpl() {
        databaseUtil.createTable("reviews");
        databaseUtil.createTable("review_likes");
    }

    //INSERT INTO reviews(id, hotel_id, rating, title, reviewText, user, date )
    @Override
    public boolean save(ReviewDTO reviewDTO) {
        String sql = PreparedStatements.INSERT_REVIEW;
        try(Connection conn = databaseUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, reviewDTO.getReviewId());
            ps.setString(2, reviewDTO.getHotelId());
            ps.setString(3, reviewDTO.getRatingOverall());
            ps.setString(4, reviewDTO.getTitle());
            ps.setString(5, reviewDTO.getReviewText());
            ps.setString(6, reviewDTO.getUserNickname());
            ps.setString(7, reviewDTO.getReviewSubmissionDate());
            ps.setInt(8, reviewDTO.getLikes());
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        } catch (SQLException e) {

            logger.error("Error in sving reviews to db",e);
        }
        return false;
    }

    @Override
    public Set<ReviewDTO> getReviewByHotelId(String hotelId) {
        Set<ReviewDTO> reviews = new TreeSet<>();
        String sql = PreparedStatements.GET_REVIEW_HOTELID;
        //reviews(id, hotel_id, rating, title, reviewText, user, date )
        try(Connection conn = databaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hotelId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ReviewDTO reviewDTO = new ReviewDTO();
                reviewDTO.setReviewId(rs.getString("id"));
                reviewDTO.setHotelId(rs.getString("hotel_id"));
                reviewDTO.setTitle(rs.getString("title"));
                reviewDTO.setReviewText(rs.getString("reviewText"));
                reviewDTO.setUserNickname(rs.getString("user"));
                reviewDTO.setReviewSubmissionDate(rs.getString("date"));
                reviewDTO.setLikes(rs.getInt("likes"));
                reviewDTO.setRatingOverall(rs.getDouble("rating"));
                reviews.add(reviewDTO);
            }
        }catch (SQLException e){
            logger.error("Error in getting reviews",e);
        }
        return reviews;
    }

    @Override
    public ReviewDTO getReviewByID(String id) {
        ReviewDTO reviewDTO = new ReviewDTO();
        String sql = PreparedStatements.GET_REVIEW;
        try(Connection conn = databaseUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                reviewDTO.setReviewId(rs.getString("id"));
                reviewDTO.setHotelId(rs.getString("hotel_id"));
                reviewDTO.setTitle(rs.getString("title"));
                reviewDTO.setReviewText(rs.getString("reviewText"));
                reviewDTO.setUserNickname(rs.getString("user"));
                reviewDTO.setReviewSubmissionDate(rs.getString("date"));
                reviewDTO.setLikes(rs.getInt("likes"));
                reviewDTO.setRatingOverall(rs.getDouble("rating"));
            }
        }catch (SQLException e){
            logger.error("Error in getting reviews",e);
        }
        return reviewDTO;
    }

    @Override
    public boolean updateReview(ReviewDTO oldReview, ReviewDTO newreviewDTO) {
        String sql = PreparedStatements.UPDATE_REVIEW;
        boolean changed = !oldReview.getReviewText().equals(newreviewDTO.getReviewText())
                || !oldReview.getTitle().equals(newreviewDTO.getTitle());
        if(changed){
            try(Connection conn = databaseUtil.getConnection()){
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, newreviewDTO.getReviewText());
                ps.setString(2, newreviewDTO.getTitle());
                ps.setString(3, newreviewDTO.getReviewId());
                ps.setString(4, newreviewDTO.getHotelId());
                int rowsChanged = ps.executeUpdate();
                return rowsChanged>0;
            }catch (SQLException e){
                logger.error("Error in updating review",e);
            }
        }
        return false;
    }

    @Override
    public boolean deleteReview(String reviewId, String hotelId) {
        String sql = PreparedStatements.DELETE_REVIEW;
        try(Connection conn = databaseUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, reviewId);
            ps.setString(2, hotelId);
            return ps.executeUpdate()>0;
        }catch (SQLException e){
            logger.error("Error in deleting review",e);
        }
        return false;
    }

    @Override
    public boolean updateLikes(String reviewId, int newLikes) {
        String sql = PreparedStatements.UPDATE_LIKES;
        try(Connection conn = databaseUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, newLikes);
            ps.setString(2, reviewId);
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        }catch (SQLException e){
            logger.error("Error in updating likes",e);
        }
        return false;
    }

    @Override
    public boolean isReviewLikedByUser(String reviewId, String username) {
        String sql = PreparedStatements.GET_REVIEW_LIKED;
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, reviewId);
             ps.setString(2, username);
             ResultSet rs = ps.executeQuery();
             return rs.next();
        } catch (SQLException e) {
            logger.error("Error in getting review likes",e);
        }
        return false;
    }

    @Override
    public boolean addLike(String reviewId, String username) {
        String sql = PreparedStatements.INSERT_REVIEW_LIKES;
        try(Connection conn = databaseUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, reviewId);
            ps.setString(2, username);
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        }catch (SQLException e){
            logger.error("Error in inserting review likes",e);
        }
        return false;
    }

    @Override
    public boolean removeLike(String reviewId, String username) {
        String sql = PreparedStatements.DELETE_REVIEW_LIKES;
        try(Connection conn = databaseUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, reviewId);
            ps.setString(2, username);
            int rowsChanged = ps.executeUpdate();
            return rowsChanged>0;
        }catch (SQLException e){
            logger.error("Error in deleting review likes",e);
        }
        return false;
    }

    @Override
    public Set<String> getLikedReviewsForUser(String username) {
        String sql = PreparedStatements.GET_ALL_LIKES_FOR_USER;
        Set<String> likedReviews = new HashSet<>();
        try(Connection conn = databaseUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                likedReviews.add(rs.getString("review_id"));
            }
        }catch (SQLException e){
            logger.error("Error in getting all reviews for user",e);
        }
        return likedReviews;
    }
}
