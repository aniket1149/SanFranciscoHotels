package hotelapp.controller;

import hotelapp.models.ReviewByWords;
import hotelapp.models.ReviewDTO;
import hotelapp.models.StopWords;

import java.time.LocalDate;
import java.util.*;

/*
Inverted Index class takes Review Files processed from Json Procesor and create Revieew objects for the same.
hotelIdReviewMap : maps hotel<ids> with Set of reviews order by date in decreasing order if equal order by review Id
wordInvertedindex : maps non-STOP_WORDS from all reviews files and maintains a set of reviews where the word appeared sorted by decreasing frequency of words appeared, date and reviewId
 */

public class InvertedIndex {
    private TreeMap<String, TreeSet<ReviewDTO>> hotelIdReviewMap;
    private HashMap<String, TreeSet<ReviewByWords>> wordInvertedIndex ;
    private Set<String> deletedReviewIds;
    private TreeMap<String, ReviewDTO> reviewIdMap;

    public InvertedIndex() {
        hotelIdReviewMap = new TreeMap<>((a,b)->b.compareTo(a));
        wordInvertedIndex = new HashMap<>();
        deletedReviewIds = new HashSet<>();
        reviewIdMap = new TreeMap<>();
    }

    public InvertedIndex(Set<ReviewDTO> reviews) {
        this();
        initializeHotelIdReviewMap(reviews);
    }

    protected void initializeInvertedIndex(ReviewDTO review) {
        String[] words = review.getReviewText().toLowerCase().split("[, /()]+");
        HashMap<String, Integer> map = new HashMap<>();
        for(String word : words){
            if(word.isBlank() || word.isEmpty() || StopWords.ENGLISH_STOP_WORDS.contains(word)) continue;
            if(map.containsKey(word)){
                map.put(word, map.get(word)+1);
            }else {
                map.put(word, 1);
            }
        }
        for(String word : map.keySet()){
            ReviewByWords rw = new ReviewByWords(word,map.get(word),review);
            wordInvertedIndex.computeIfAbsent(word, k -> new TreeSet<>()).add(rw);
        }
        return;
    }

    protected void initializeHotelIdReviewMap(Set<ReviewDTO> reviews) {
        for(ReviewDTO review : reviews) {
            reviewIdMap.put(review.getReviewId(), review);
            hotelIdReviewMap.computeIfAbsent(review.getHotelId(), k -> new TreeSet<>()).add(review);
            initializeInvertedIndex(review);

        }
    }

    protected String getUserName(String reviewId) {
        if(reviewIdMap.containsKey(reviewId))
            return reviewIdMap.get(reviewId).getUserNickname();
        return null;
    }

    protected Set<ReviewDTO> getReviewByHotelId(String hotelId) {
        if(hotelIdReviewMap.containsKey(hotelId)) return Collections.unmodifiableSet(hotelIdReviewMap.get(hotelId));
        return null;
    }

    public String findReviewsForHotels(String query){
        if(!hotelIdReviewMap.containsKey(query)){throw new IllegalArgumentException();}
        TreeSet<ReviewDTO> reviews = hotelIdReviewMap.get(query);
        StringBuilder stringBuilder = new StringBuilder();
        for(ReviewDTO review : reviews) {
            stringBuilder.append("--------------------"+ System.lineSeparator() + review);

            stringBuilder.toString();
        }

         return stringBuilder.toString();
    }


    public String findWordInReviews(String queryPart) {
        TreeSet<ReviewByWords> reviews = wordInvertedIndex.get(queryPart);
        StringBuilder stringBuilder = new StringBuilder();
        if(null == reviews) return "No such word exists in any reviews.";
        for(ReviewByWords revWords : reviews){
            if(deletedReviewIds.contains(revWords.getReviewObject().getReviewId())){
                continue;
            }
            stringBuilder.append("--------------------"+ System.lineSeparator() + revWords.getFrequency() + System.lineSeparator());
            stringBuilder.append(revWords.getReview().toString());

        }
        TreeSet<ReviewByWords> newReviews = wordInvertedIndex.get(queryPart);
        TreeSet<ReviewByWords> oldReviews = wordInvertedIndex.get(queryPart);
        for(ReviewByWords revWords : oldReviews){
            if(deletedReviewIds.contains(revWords.getReviewId())){
                newReviews.remove(revWords);
            }
        }
        return stringBuilder.toString();
    }

    public List<ReviewDTO> findWordFreq(String queryPart) {
        TreeSet<ReviewByWords> reviews = wordInvertedIndex.get(queryPart);
        if(null == reviews) return null;
        List<ReviewDTO> result = new ArrayList<>();
        for(ReviewByWords revWords : reviews){
            result.add(revWords.getReviewObject());
        }
        return result;
    }

    public boolean deleteReview(String hotelId, String reviewId) {
        boolean result = false;
        ReviewDTO targetReview = null;
        if(hotelIdReviewMap.containsKey(hotelId)) {
            TreeSet<ReviewDTO> reviews = hotelIdReviewMap.get(hotelId);
            for(ReviewDTO review : reviews) {
                if(review.getReviewId().equals(reviewId)) {
                    targetReview = review;
                    deletedReviewIds.add(reviewId);
                }
            }
            reviews.remove(targetReview);
        }
        return targetReview != null;
    }

    protected boolean updateReview(String reviewId, ReviewDTO newReview) {
        ReviewDTO targetReview = null;
        if(reviewIdMap.containsKey(reviewId)) {
            targetReview = reviewIdMap.get(reviewId);
        }
        if(null == targetReview) {return false;}
        if(!targetReview.getReviewText().equals(newReview.getReviewText())) {
            targetReview.setReviewText(newReview.getReviewText());
        }
        if(!targetReview.getTitle().equals(newReview.getTitle())) {
            targetReview.setTitle(newReview.getTitle());
        }
        targetReview.setReviewSubmissionDate(LocalDate.now().toString());
        return true;
    }

    protected ReviewDTO editableReview(String reviewId) {
        if(reviewIdMap.containsKey(reviewId)) {
            return new ReviewDTO(reviewIdMap.get(reviewId));
        }
        return null;
    }
}
