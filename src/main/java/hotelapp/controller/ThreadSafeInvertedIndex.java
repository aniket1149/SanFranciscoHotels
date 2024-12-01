package hotelapp.controller;

import hotelapp.models.ReviewDTO;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/*
* Thread safe extended version  to store review specific data.
* No parallel writes.
* ReentrantReadWrite Lock acts as a synchronizer for this class
* Calls upon parent class methods to make it threadsafe
* */
public class ThreadSafeInvertedIndex extends InvertedIndex{
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public ThreadSafeInvertedIndex() {
        super();
    }

    public void addReviews(Set<ReviewDTO> reviews) {
        lock.writeLock().lock();
        try{
            super.initializeHotelIdReviewMap(reviews);
        }finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Set<ReviewDTO> getReviewByHotelId(String hotelId) {
        Set<ReviewDTO> res = super.getReviewByHotelId(hotelId);
        if(res== null) return null;
        else return Collections.unmodifiableSet(res);
    }

    public TreeSet<ReviewDTO> getReviewByHotelId(String hotelId, int numberOfReviews) {
        Set<ReviewDTO> res = super.getReviewByHotelId(hotelId);
        if(res == null) return null;
        TreeSet<ReviewDTO> ordered = new TreeSet<>(res);
        return ordered.stream().limit(numberOfReviews).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String findReviewsForHotels(String query){
        lock.readLock().lock();
        try {
            return super.findReviewsForHotels(query);
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String getUserName(String reviewId) {
        lock.readLock().lock();
        try {
            return super.getUserName(reviewId);
        }finally {
            lock.readLock().unlock();
        }
    }



    @Override
    public String findWordInReviews(String query){
        lock.readLock().lock();
        try {
            return super.findWordInReviews(query);
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean updateReview(String reviewId, ReviewDTO newReview) {
        lock.writeLock().lock();
        try {
            return super.updateReview(reviewId, newReview);
        }finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ReviewDTO editableReview(String reviewId){
        lock.readLock().lock();
        try {
            return super.editableReview(reviewId);
        }finally {
            lock.readLock().unlock();
        }
    }

    public List<ReviewDTO> findWordInReviews(String query, int numberOfReviews){
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(super.findWordFreq(query).stream().limit(numberOfReviews).collect(Collectors.toList()));
        }finally {
            lock.readLock().unlock();
        }
    }


}
