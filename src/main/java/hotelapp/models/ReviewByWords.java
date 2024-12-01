package hotelapp.models;

import java.time.LocalDate;

public class ReviewByWords implements Comparable<ReviewByWords> {
    private String words;
    private Integer frequency;
    private ReviewDTO review;

    public ReviewByWords(String words, Integer frequency, ReviewDTO review) {
        this.words = words;
        this.frequency = frequency;
        this.review = review;
    }

    @Override
    public int compareTo(ReviewByWords o) {
        int res = Integer.compare(o.frequency, this.frequency);
        if(res == 0){
            res = LocalDate.parse(o.review.getReviewSubmissionDate()).compareTo(LocalDate.parse(this.review.getReviewSubmissionDate()));
            if(res == 0){
                return this.review.getReviewId().compareTo(o.review.getReviewId());
            }
            return res;
        }
        return res;
    }

    public String getWords() {
        return words;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public String getReview() {
        return review.toString();
    }
    public ReviewDTO getReviewObject() {
        return this.review;
    }
}
