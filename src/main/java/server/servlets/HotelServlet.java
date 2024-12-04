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
    private HotelCollection hotelCollection;
    private ThreadSafeInvertedIndex reviewCollection;
    private VelocityTemplateEngine templateEngine = new VelocityTemplateEngine();
    private HotelServlet() {
    }

    public HotelServlet(HotelCollection hotelCollection, ThreadSafeInvertedIndex reviewCollection) {
        this.hotelCollection = hotelCollection;
        this.reviewCollection = reviewCollection;
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
        HotelDTO hotel = hotelCollection.findHotelById(hotelId);
        if (hotel == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hotel not found");
            return;
        }
        HttpSession session = request.getSession();
        User user = null;

        if((user = (User) session.getAttribute("user")) == null) {
            response.sendRedirect("/user/login");
            return;
        }
        Set<ReviewDTO> reviewDTOS = reviewCollection.getReviewByHotelId(hotelId);
        double averageRating = calculateAverageRating(reviewDTOS);
        String expediaLink = generateExpediaLink(hotel);
        Map<String, Object> model = new HashMap<>();
        model.put("hotel", hotel);
        model.put("reviews", (reviewDTOS!=null) ? reviewDTOS : new ArrayList<>() );
        model.put("averageRating", averageRating);
        model.put("expediaLink", expediaLink);
        model.put("loggedInUser", user.getUsername());

        templateEngine.render("templates/hotel_details.vm", model, request, response);
    }

    private String generateExpediaLink(HotelDTO hotel) {
        String cityName = hotel.getCity().replace(" ", "-");
        String hotelName = hotel.getName().replace(" ", "-");
        return "https://www.expedia.com/" + cityName + "-Hotels-" + hotelName + ".h" + hotel.getId() + ".Hotel-Information";
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
