package server.servlets;

import hotelapp.controller.ThreadSafeInvertedIndex;
import hotelapp.models.ReviewDTO;
import hotelapp.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.TravelServer;
import server.utils.PasswordUtil;
import server.utils.VelocityTemplateEngine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Review Servlet to serve reviews for the hotel.
 * Handles Add, Update, Delete Reviews.
 * **/

public class ReviewServlet extends HttpServlet {
    private ThreadSafeInvertedIndex reviewCollection;
    private static final Logger logger = LogManager.getLogger(TravelServer.class);
    private VelocityTemplateEngine templateEngine = new VelocityTemplateEngine();
    private ReviewServlet() {
    }
    public ReviewServlet(ThreadSafeInvertedIndex reviewCollection) {
        this.reviewCollection = reviewCollection;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/user/login");
            return;
        }

        String query = request.getParameter("id");
        if (query == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        showEditForm(request, response);
    }

        @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/user/login");
            return;
        }
        String action = request.getPathInfo();
        if (action == null) action = "/add";
        switch (action) {
            case "/add":
                handleAddReview(request, response);
                break;
            case "/delete":
                handleDeleteReview(request, response);
                break;
            case "/edit":
                processEditForm(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleDeleteReview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String hotelId = request.getParameter("hotelId");
        String reviewId = request.getParameter("reviewId");

        if (hotelId == null || reviewId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
        }

        if(!reviewCollection.getUserName(reviewId).equals(user.getUsername())){
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("/hotel/" + hotelId);

            return;
        }

        if(reviewCollection.deleteReview(hotelId, reviewId)) {
            logger.info("Review deleted successfully");
        }else{
            response.sendError(HttpServletResponse.SC_NOT_FOUND );
            return;
        }
        response.sendRedirect("/hotel/" + hotelId);

    }

    private void processEditForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reviewId = request.getParameter("reviewId");
        String title = request.getParameter("title");
        String text = request.getParameter("reviewText");

        ReviewDTO newReviewDto = reviewCollection.editableReview(reviewId);
        if(newReviewDto == null){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        newReviewDto.setTitle(title);
        newReviewDto.setReviewText(text);
        if(reviewCollection.updateReview(reviewId, newReviewDto)){
            response.sendRedirect("/hotel/" + newReviewDto.getHotelId());
        }else{
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reviewId = request.getParameter("id");
        ReviewDTO review = reviewCollection.editableReview(reviewId);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (!review.getUserNickname().equals(user.getUsername())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to edit this review");
            return;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("review", review);

        templateEngine.render("templates/edit_review.vm", model, request, response);
    }

    private void handleAddReview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = request.getParameter("hotelId");
        String title = request.getParameter("title");
        String text = request.getParameter("text");
        Double rating = Double.parseDouble(request.getParameter("rating")+".0");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setHotelId(hotelId);
        reviewDTO.setTitle(title);
        reviewDTO.setReviewId(PasswordUtil.generateSalt());
        reviewDTO.setReviewText(text);
        reviewDTO.setRatingOverall(rating);
        reviewDTO.setUserNickname(user.getUsername());
        reviewDTO.setReviewSubmissionDate(LocalDate.now().toString());

        Set<ReviewDTO> reviewSet = new HashSet<>();
        reviewSet.add(reviewDTO);
        try{
            reviewCollection.addReviews(reviewSet);
        }catch (Exception e){
            logger.error(e);
        }
        response.sendRedirect("/hotel/" + hotelId);
    }
}
