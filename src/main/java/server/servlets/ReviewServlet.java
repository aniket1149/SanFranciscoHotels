package server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import server.repositories.ReviewRepository;
import server.repositories.ReviewRepositoryImpl;
import server.utils.PasswordUtil;
import server.utils.VelocityTemplateEngine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Review Servlet to serve reviews for the hotel.
 * Handles Add, Update, Delete Reviews.
 * **/

public class ReviewServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ReviewServlet.class);
    private VelocityTemplateEngine templateEngine = new VelocityTemplateEngine();
    private ReviewRepository reviewRepository;
    private static int getAllCount=0;
    private ReviewServlet() {
    }
    public ReviewServlet(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getPathInfo();
        if (action == null) action = "/list";
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/user/login");
            return;
        }

        switch (action){
            case "/list":
                handlePaginatedReviews(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            case "/total":
                totalPages(request, response);
                break;
        }
    }

    private void totalPages(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = request.getParameter("hotelId");
        if (hotelId == null || hotelId.equals("")) {
            System.out.println("hotelId is null or empty");
            response.sendRedirect("/search");
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("total", reviewRepository.getCountforReviews(hotelId));
        response.setContentType("application/json");
        response.getWriter().write(jsonObject.toString());
    }

    private void handlePaginatedReviews(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hotelId = request.getParameter("hotelId");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/user/login");
            return;
        }
        if (hotelId == null || hotelId.equals("")) {
            System.out.println("hotelId is null or empty");
            return;
        }

        final int limit = 2;

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try{
                page = Integer.parseInt(pageParam);
            }catch (NumberFormatException e){
                System.out.println("pageParam is null or empty");
                page = 1;
            }
        }

        if(page==1){
            getAllCount  = Integer.parseInt(reviewRepository.getCountforReviews(hotelId));
        }
        int totalPages = (int) Math.ceil(getAllCount/2);
        int offset = (page - 1) * limit;
        int remaining = getAllCount-limit;
        getAllCount = remaining;
        List<ReviewDTO> reviews = reviewRepository.getPaginatedReviews(hotelId,user.getUsername(),limit,offset);
        Gson gson = new Gson();
        response.setContentType("application/json");
        JsonObject jsonObject = new JsonObject();
        if(reviews.isEmpty()){
            jsonObject.addProperty("noResults", true);
            response.getWriter().write(jsonObject.toString());
            return;
        }
        if(remaining>0){
            jsonObject.addProperty("hasNext", true);
        }else jsonObject.addProperty("hasNext", false);
        if(offset>0){
            jsonObject.addProperty("hasPrevious", true);
        }else{
            jsonObject.addProperty("hasPrevious", false);
        }if((remaining+Math.abs(offset))<Integer.parseInt(reviewRepository.getCountforReviews(hotelId))){
            jsonObject.addProperty("hasNext", true);
        }
        jsonObject.add("reviews", gson.toJsonTree(reviews));
        response.getWriter().println(jsonObject);
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
            case "/like":
                handleLikeClicked(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLikeClicked(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reviewId = request.getParameter("reviewId");
        if (reviewId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing reviewId");
            return;
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user == null) {
            response.sendRedirect("/user/login");
            return;
        }
        ReviewDTO review = reviewRepository.getReviewByID(reviewId);
        if (review == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        boolean currentlyLiked = reviewRepository.isReviewLikedByUser(reviewId, user.getUsername());
        int currentLikes = review.getLikes();
        if(currentlyLiked) {
            if (reviewRepository.removeLike(reviewId, user.getUsername())){
                currentLikes = currentLikes > 0 ? currentLikes - 1 : 0;
                reviewRepository.updateLikes(reviewId, currentLikes);
            }
        }else{
            if(reviewRepository.addLike(reviewId, user.getUsername())){
                currentLikes = currentLikes + 1;
                reviewRepository.updateLikes(reviewId, currentLikes);
            }
        }
        boolean likedNow = !currentlyLiked;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = String.format("{\"liked\": %b, \"likes\": %d}", likedNow, currentLikes);
        response.getWriter().write(json);
    }

    private void handleDeleteReview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String hotelId = request.getParameter("hotelId");
        String reviewId = request.getParameter("reviewId");

        if (reviewId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
        }

        ReviewDTO reviewDTO = reviewRepository.getReviewByID(reviewId);

       if(!reviewDTO.getUserNickname().equals(user.getUsername())){
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect("/hotel/" + hotelId);
            return;
       }
       if(reviewDTO.getLikes()>0){
           reviewRepository.removeLike(reviewId, user.getUsername());
       }
       JsonObject jsonObject = new JsonObject();

        if(reviewRepository.deleteReview(reviewDTO.getReviewId(), reviewDTO.getHotelId())) {
            jsonObject.addProperty("success", true);
            response.setContentType("application/json");
            response.getWriter().println(new Gson().toJsonTree(jsonObject));
           logger.info("Review {} deleted successfully ", reviewDTO.getReviewId());
          }else{
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                response.sendRedirect("/hotel/" + hotelId);
                return;
        }
    }

    private void processEditForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reviewId = request.getParameter("reviewId");
        String title = request.getParameter("title");
        String text = request.getParameter("reviewText");

       // ReviewDTO newReviewDto = reviewCollection.editableReview(reviewId);
        ReviewDTO oldReviewDto = reviewRepository.getReviewByID(reviewId);
        ReviewDTO newReviewDto = new ReviewDTO(oldReviewDto);
        if(newReviewDto == null){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        JsonObject jsonObject = new JsonObject();
        newReviewDto.setTitle(title);
        newReviewDto.setReviewText(text);
        if(reviewRepository.updateReview(oldReviewDto, newReviewDto)){
            jsonObject.addProperty("success", true);
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(jsonObject));
        }else{
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reviewId = request.getParameter("id");
        if (reviewId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        ReviewDTO review = reviewRepository.getReviewByID(reviewId);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (!review.getUserNickname().equals(user.getUsername())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to edit this review");
            return;
        }
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().println(gson.toJsonTree(review));
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
        reviewDTO.setLikes(0);

//        Set<ReviewDTO> reviewSet = new HashSet<>();
//        reviewSet.add(reviewDTO);
        try{
 //           reviewCollection.addReviews(reviewSet);
            reviewRepository.save(reviewDTO);
        }catch (Exception e){
            logger.error(e);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", reviewDTO != null);
        response.setContentType("application/json");
        response.getWriter().write(jsonObject.toString());
    }
}
