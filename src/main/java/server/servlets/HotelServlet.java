package server.servlets;

import hotelapp.controller.HotelCollection;
import hotelapp.controller.ThreadSafeInvertedIndex;
import hotelapp.models.HotelDTO;
import hotelapp.models.ReviewDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.utils.VelocityTemplateEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        Set<ReviewDTO> reviewDTOS = reviewCollection.getReviewByHotelId(hotelId);
        double averageRating = calculateAverageRating(reviewDTOS);
        String expediaLink = generateExpediaLink(hotel);
        Map<String, Object> model = new HashMap<>();
        model.put("hotel", hotel);
        model.put("reviews", reviewDTOS);
        model.put("averageRating", averageRating);
        model.put("expediaLink", expediaLink);

        templateEngine.render("templates/hotel_details.vm", model, response);
    }

    private String generateExpediaLink(HotelDTO hotel) {
        String cityName = hotel.getCity().replace(" ", "-");
        String hotelName = hotel.getName().replace(" ", "-");
        return "https://www.expedia.com/" + cityName + "-Hotels-" + hotelName + ".h" + hotel.getId() + ".Hotel-Information";
    }

    private double calculateAverageRating(Set<ReviewDTO> reviews) {
        if (reviews.isEmpty()) return 0.0;
        double sum = 0.0;
        for (ReviewDTO review : reviews) {
            sum+= Double.valueOf(review.getRatingOverall());
        }
        return sum / reviews.size();
    }
}
