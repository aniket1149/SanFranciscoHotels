package server.servlets;

import hotelapp.controller.ThreadSafeInvertedIndex;
import jakarta.servlet.http.HttpServlet;

public class ReviewServlet extends HttpServlet {
    private ThreadSafeInvertedIndex reviewCollection;

    private ReviewServlet() {
    }
    public ReviewServlet(ThreadSafeInvertedIndex reviewCollection) {
        this.reviewCollection = reviewCollection;
    }
}
