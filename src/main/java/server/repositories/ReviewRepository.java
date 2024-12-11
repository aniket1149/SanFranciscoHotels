package server.repositories;
import hotelapp.models.ReviewDTO;

import java.util.List;
import java.util.Set;

public interface ReviewRepository {
    /**
     * Saves the review
     * @param reviewDTO
     * @return boolean based on rows affected.
     */
    boolean save(ReviewDTO reviewDTO);

    /**
     * Gets all the reviews for a hotelId
     * @param hotelId
     * @return
     */
    Set<ReviewDTO> getReviewByHotelId(String hotelId);

    /**
     * Gets the review by Id
     * @param id
     * @return
     */
    ReviewDTO getReviewByID(String id);

    /**
     * Updates the reviews based on the oldReview and New Review.
     * @param oldReview
     * @param newReview
     * @return
     */
    boolean updateReview(ReviewDTO oldReview, ReviewDTO newReview);

    /**
     * Deletes the selected review
     * @param reviewId
     * @param hotelId
     * @return boolean based on rows affected.
     */
    boolean deleteReview(String reviewId, String hotelId );

    /**
     * Updates the number of likes.
     * @param reviewId
     * @param newLikes
     * @return
     */
    boolean updateLikes (String reviewId, int newLikes);

    /**
     * Checks if a review is liked by the user.
     * @param reviewId
     * @param username
     * @return
     */
    boolean isReviewLikedByUser(String reviewId, String username);

    /**
     * Adds like
     * @param reviewId
     * @param username
     * @return
     */
    boolean addLike(String reviewId, String username);

    /**
     * Removes like for a reviewId
     * @param reviewId
     * @param username
     * @return
     */
    boolean removeLike(String reviewId, String username);

    /**
     * Gets Liked Reviews for a user.
     * @param username
     * @return
     */
    Set<String> getLikedReviewsForUser(String username);

    /**
     * Gets paginated reviews based on current page.
     * @param hotelId
     * @param userName
     * @param limit
     * @param offset
     * @return
     */
    List<ReviewDTO> getPaginatedReviews(String hotelId, String userName, int limit, int offset);

    /**
     * Gets counts of total reviews for a particular hotelId.
     * @param hotelId
     * @return
     */
    String getCountforReviews(String hotelId);
}
