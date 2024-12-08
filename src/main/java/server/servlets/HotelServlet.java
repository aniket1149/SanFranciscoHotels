package server.servlets;

import hotelapp.controller.HotelCollection;
import hotelapp.controller.ThreadSafeInvertedIndex;
import hotelapp.models.HotelDTO;
import hotelapp.models.ReviewDTO;
import hotelapp.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import server.repositories.HotelRepository;
import server.repositories.HotelRepositoryImpl;
import server.repositories.ReviewRepository;
import server.repositories.ReviewRepositoryImpl;
import server.utils.VelocityTemplateEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * holds the functionality for Hotel server. Maps to -> /hotel/*
 */
public class HotelServlet extends HttpServlet {
    private VelocityTemplateEngine templateEngine = new VelocityTemplateEngine();
    private HotelRepository hotelRepository;
    private ReviewRepository reviewRepository;
    private HotelServlet() {
    }

    public HotelServlet(HotelRepository hotelRepository, ReviewRepository reviewRepository) {
        this.hotelRepository = hotelRepository;
        this.reviewRepository = reviewRepository;
    }
    /**
     * Gets the hotel based on the hotelId. Along with the reviews.
     * */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect("/search");
            return;
        }
        String hotelId = pathInfo.substring(1);
        //HotelDTO hotel = hotelCollection.findHotelById(hotelId);
        HotelDTO hotel = hotelRepository.findHotelById(hotelId);

        if (hotel == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hotel not found");
            return;
        }
        HttpSession session = request.getSession();
        User user = null;
        Map<String, Boolean> userLikesMap = new HashMap<>();
        if((user = (User) session.getAttribute("user")) == null) {
            response.sendRedirect("/user/login");
            return;
        }
        Set<String> userLikedReviews = reviewRepository.getLikedReviewsForUser(user.getUsername());
        Set<ReviewDTO> reviewDTOS = reviewRepository.getReviewByHotelId(hotelId);
        for (ReviewDTO reviewDTO : reviewDTOS) {
            userLikesMap.put(reviewDTO.getReviewId(), userLikedReviews.contains(reviewDTO.getReviewId()));
        }
        double averageRating = calculateAverageRating(reviewDTOS);
        Map<String, Object> model = new HashMap<>();
        model.put("hotel", hotel);
        model.put("reviews", (reviewDTOS!=null) ? reviewDTOS : new ArrayList<>() );
        model.put("averageRating", averageRating);
        model.put("loggedInUser", user.getUsername());
        model.put("userLikesMap", userLikesMap);

        templateEngine.render("templates/hotel_details.vm", model, request, response);
    }



    private double calculateAverageRating(Set<ReviewDTO> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        double sum = 0.0;
        for (ReviewDTO review : reviews) {
            sum+= Double.valueOf(review.getRatingOverall());
        }
        return sum / reviews.size();
    }
}
