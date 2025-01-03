package hotelapp.models;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ReviewDTO implements Comparable<ReviewDTO> {
    private String hotelId;
    private String reviewId;
    private Double ratingOverall;
    private String userNickname;
    private String title;
    private String reviewText;
    private String reviewSubmissionDate;
    private int likes;
    private String liked;
    private Double averageRating;

    public String getReviewSubmissionDate() {
        return reviewSubmissionDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getTitle() {
        return title;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public String getRatingOverall() {
        return String.valueOf(ratingOverall);
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getHotelId() {
        return hotelId;
    }


    public ReviewDTO(){
    }

    public ReviewDTO(ReviewDTO duplicate){
        this.hotelId = duplicate.getHotelId();
        this.reviewId = duplicate.getReviewId();
        this.userNickname = duplicate.getUserNickname();
        this.title = duplicate.getTitle();
        this.reviewText = duplicate.getReviewText();
        this.reviewSubmissionDate = duplicate.getReviewSubmissionDate();
    }

    @Override
    public int compareTo(ReviewDTO o) {
        try{
            int res = LocalDate.parse(o.getReviewSubmissionDate()).compareTo(LocalDate.parse(this.reviewSubmissionDate));
            if(res == 0) {
                return this.reviewId.compareTo(o.getReviewId());
            }
            return res;
        } catch (Exception e ){
            System.out.println("Error in sorting. Sorting the object based on reviewId with id: "+this.reviewId+" and other review id: " + o.reviewId);
            System.out.println(e.getMessage());
            return this.reviewId.compareTo(o.getReviewId());
        }
    }

    @Override
    public String toString() {
        return "Review by " + ((this.getUserNickname() == null || this.getUserNickname().isEmpty()) ? "Anonymous" : this.getUserNickname()) + " on " + this.getReviewSubmissionDate() + System.lineSeparator()+
                "Rating: " + this.getRatingOverall().replace(".0","") + System.lineSeparator() +
                "ReviewId: " +this.getReviewId()+ System.lineSeparator() +
                this.getTitle() + System.lineSeparator()+
                this.getReviewText() + System.lineSeparator() ;
    }
}
