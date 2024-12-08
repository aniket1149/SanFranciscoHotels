package server.repositories;
import hotelapp.models.ReviewDTO;
import java.util.Set;

public interface ReviewRepository {
    boolean save(ReviewDTO reviewDTO);
    Set<ReviewDTO> getReviewByHotelId(String hotelId);
    ReviewDTO getReviewByID(String id);
    boolean updateReview(ReviewDTO oldReview, ReviewDTO newReview);
    boolean deleteReview(String reviewId, String hotelId );
    boolean updateLikes (String reviewId, int newLikes);
    boolean isReviewLikedByUser(String reviewId, String username);
    boolean addLike(String reviewId, String username);
    boolean removeLike(String reviewId, String username);
    Set<String> getLikedReviewsForUser(String username);
}
